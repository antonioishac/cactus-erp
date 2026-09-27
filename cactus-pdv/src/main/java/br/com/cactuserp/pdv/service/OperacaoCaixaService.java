package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvSangria;
import br.com.cactuserp.pdv.entity.PdvSuprimento;
import br.com.cactuserp.pdv.entity.TipoOperacaoCaixa;
import br.com.cactuserp.pdv.repository.PdvMovimentoRepository;
import br.com.cactuserp.pdv.repository.PdvSangriaRepository;
import br.com.cactuserp.pdv.repository.PdvSuprimentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Serviço de operações de movimentação de caixa: sangria e suprimento.
 *
 * <p>Trata as duas operações de forma unificada porque elas são irmãs
 * estruturais (mesma forma de cabeçalho, diferem apenas no significado).
 *
 * <h3>Regras de autorização</h3>
 * <ul>
 *   <li><b>Suprimento</b>: operador autoriza sozinho.</li>
 *   <li><b>Sangria</b>: operador autoriza sozinho se valor ≤
 *       {@link #LIMITE_SANGRIA_SEM_AUTORIZACAO}; acima disso exige gerente.</li>
 * </ul>
 *
 * <h3>Consistência fiscal</h3>
 * Cada registro de sangria/suprimento atualiza, na MESMA transação, os
 * totalizadores agregados em {@link PdvMovimento#getTotalSangria()} /
 * {@link PdvMovimento#getTotalSuprimento()}. Isso garante que o relatório Z
 * (fechamento de caixa) leia valores já consolidados e que o fisco encontre
 * coerência entre os registros analíticos e o agregado.
 */
@Service
@Transactional(readOnly = true)
public class OperacaoCaixaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperacaoCaixaService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Limite (em reais) abaixo do qual o operador pode fazer sangria sem
     * autorização gerencial. Acima deste valor, exige gerente/supervisor.
     *
     * <p>TODO: futuramente migrar para coluna parametrizável em
     * {@code PDV_CONFIGURACAO}.
     */
    public static final BigDecimal LIMITE_SANGRIA_SEM_AUTORIZACAO = new BigDecimal("200.00");

    /** Tamanho mínimo da observação (auditoria). */
    private static final int TAMANHO_MINIMO_OBSERVACAO = 5;

    private final PdvSangriaRepository sangriaRepository;
    private final PdvSuprimentoRepository suprimentoRepository;
    private final PdvMovimentoRepository movimentoRepository;
    private final SessaoPdv sessaoPdv;
    private final DispositivoInfoService dispositivoInfo;

    public OperacaoCaixaService(PdvSangriaRepository sangriaRepository,
                                PdvSuprimentoRepository suprimentoRepository,
                                PdvMovimentoRepository movimentoRepository,
                                SessaoPdv sessaoPdv,
                                DispositivoInfoService dispositivoInfo) {
        this.sangriaRepository = sangriaRepository;
        this.suprimentoRepository = suprimentoRepository;
        this.movimentoRepository = movimentoRepository;
        this.sessaoPdv = sessaoPdv;
        this.dispositivoInfo = dispositivoInfo;
    }

    // ====================================================================
    //  CONSULTAS — regras de autorização
    // ====================================================================

    /**
     * Indica se o tipo + valor exigem autorização gerencial.
     * Usado pela UI para decidir se deve abrir o modal de senha.
     */
    public boolean exigeAutorizacaoGerencial(TipoOperacaoCaixa tipo, BigDecimal valor) {
        if (tipo != TipoOperacaoCaixa.SANGRIA) {
            return false;
        }
        if (valor == null) return false;
        return valor.compareTo(LIMITE_SANGRIA_SEM_AUTORIZACAO) > 0;
    }

    // ====================================================================
    //  ESCRITA — Sangria
    // ====================================================================

    /**
     * Registra uma sangria no caixa e acumula o valor em
     * {@code PDV_MOVIMENTO.TOTAL_SANGRIA} (mesma transação).
     */
    @Transactional
    public PdvSangria registrarSangria(BigDecimal valor,
                                       String observacao,
                                       PdvOperador gerenteAutorizador) {
        validarParametrosBasicos(valor, observacao);

        boolean precisaAutorizacao = exigeAutorizacaoGerencial(TipoOperacaoCaixa.SANGRIA, valor);
        if (precisaAutorizacao && gerenteAutorizador == null) {
            throw new IllegalStateException(
                    "Sangrias acima de R$ " + LIMITE_SANGRIA_SEM_AUTORIZACAO
                            + " exigem autorização de gerente/supervisor.");
        }

        // Recarrega o movimento pelo ID, garantindo entidade gerenciada
        // nesta transação (escapa de proxy lazy da sessão da UI).
        PdvMovimento movimento = recarregarMovimentoAtivo();
        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();

        PdvSangria s = new PdvSangria();
        s.setPdvMovimento(movimento);
        s.setOperador(operadorAtual);
        s.setGerenteAutorizador(gerenteAutorizador);
        s.setDataSangria(LocalDate.now());
        s.setHoraSangria(LocalTime.now().format(FMT_HORA));
        s.setValor(valor);
        s.setObservacao(observacao);
        s.setIdDispositivo(dispositivoInfo.getHostname());

        s = sangriaRepository.save(s);

        // Atualiza o totalizador agregado do movimento na mesma transação
        BigDecimal novoTotal = nullSafe(movimento.getTotalSangria()).add(valor);
        movimento.setTotalSangria(novoTotal);
        movimento = movimentoRepository.save(movimento);

        // Reflete o novo estado em memória para a UI
        sessaoPdv.atualizarMovimento(movimento);

        LOGGER.info("Sangria registrada: ID={}, valor={}, total acumulado={}, operador={}, autorizador={}",
                s.getId(), valor, novoTotal,
                operadorAtual != null ? operadorAtual.getId() : null,
                gerenteAutorizador != null ? gerenteAutorizador.getId() : "—");
        return s;
    }

    // ====================================================================
    //  ESCRITA — Suprimento
    // ====================================================================

    /**
     * Registra um suprimento (reforço de troco) no caixa e acumula o valor
     * em {@code PDV_MOVIMENTO.TOTAL_SUPRIMENTO} (mesma transação).
     */
    @Transactional
    public PdvSuprimento registrarSuprimento(BigDecimal valor,
                                             String observacao,
                                             PdvOperador gerenteAutorizador) {
        validarParametrosBasicos(valor, observacao);

        PdvMovimento movimento = recarregarMovimentoAtivo();
        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();

        PdvSuprimento s = new PdvSuprimento();
        s.setPdvMovimento(movimento);
        s.setOperador(operadorAtual);
        s.setGerenteAutorizador(gerenteAutorizador);
        s.setDataSuprimento(LocalDate.now());
        s.setHoraSuprimento(LocalTime.now().format(FMT_HORA));
        s.setValor(valor);
        s.setObservacao(observacao);
        s.setIdDispositivo(dispositivoInfo.getHostname());

        s = suprimentoRepository.save(s);

        // Atualiza o totalizador agregado do movimento na mesma transação
        BigDecimal novoTotal = nullSafe(movimento.getTotalSuprimento()).add(valor);
        movimento.setTotalSuprimento(novoTotal);
        movimento = movimentoRepository.save(movimento);

        // Reflete o novo estado em memória para a UI
        sessaoPdv.atualizarMovimento(movimento);

        LOGGER.info("Suprimento registrado: ID={}, valor={}, total acumulado={}, operador={}",
                s.getId(), valor, novoTotal,
                operadorAtual != null ? operadorAtual.getId() : null);
        return s;
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    /**
     * Recarrega o movimento ativo pelo ID na transação atual.
     * Evita LazyInitializationException com proxy vindo da SessaoPdv.
     */
    private PdvMovimento recarregarMovimentoAtivo() {
        Long id = sessaoPdv.getIdMovimentoAtivo();
        if (id == null) {
            throw new IllegalStateException("Nenhum movimento ativo na sessão.");
        }
        return movimentoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movimento #" + id + " não encontrado no banco."));
    }

    private void validarParametrosBasicos(BigDecimal valor, String observacao) {
        if (!sessaoPdv.movimentoEstaAberto()) {
            throw new IllegalStateException(
                    "Não há movimento de caixa aberto. Inicie o movimento antes.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
        }
        if (observacao == null || observacao.trim().length() < TAMANHO_MINIMO_OBSERVACAO) {
            throw new IllegalArgumentException(
                    "Observação é obrigatória (mínimo " + TAMANHO_MINIMO_OBSERVACAO
                            + " caracteres).");
        }
    }

    private BigDecimal nullSafe(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}