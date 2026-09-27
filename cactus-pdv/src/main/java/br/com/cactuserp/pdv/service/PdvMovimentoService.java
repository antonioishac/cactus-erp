package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.*;
import br.com.cactuserp.pdv.repository.PdvCaixaRepository;
import br.com.cactuserp.pdv.repository.PdvMovimentoRepository;
import br.com.cactuserp.pdv.repository.PdvOperadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PdvMovimentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdvMovimentoService.class);
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final PdvOperadorRepository operadorRepository;
    private final PdvCaixaRepository caixaRepository;
    private final PdvMovimentoRepository movimentoRepository;
    private final LoginService loginService;
    private final LicencaService licencaService;

    public PdvMovimentoService(PdvOperadorRepository operadorRepository,
                               PdvCaixaRepository caixaRepository,
                               PdvMovimentoRepository movimentoRepository,
                               LoginService loginService,
                               LicencaService licencaService) {
        this.operadorRepository = operadorRepository;
        this.caixaRepository = caixaRepository;
        this.movimentoRepository = movimentoRepository;
        this.loginService = loginService;
        this.licencaService = licencaService;
    }

    public PdvOperador autenticarGerenteSupervisor(String login, String senha) {
        return loginService.autenticarGerenteSupervisor(login, senha);
    }

    public List<PdvOperador> listarOperadoresDisponiveis() {
        return operadorRepository.findByNiveis(List.of(
                NivelAutorizacao.O, NivelAutorizacao.S, NivelAutorizacao.G));
    }

    public List<PdvCaixa> listarCaixas() {
        return caixaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public PdvMovimento abrirMovimento(PdvOperador operador,
                                       PdvCaixa caixa,
                                       PdvOperador gerenteSupervisor,
                                       BigDecimal valorInicial,
                                       String idDispositivo) {

        if (operador == null) throw new IllegalArgumentException("Selecione o operador.");
        if (caixa == null)    throw new IllegalArgumentException("Selecione o caixa.");
        if (gerenteSupervisor == null)
            throw new IllegalArgumentException("Autorização do gerente/supervisor é obrigatória.");
        if (valorInicial == null) valorInicial = BigDecimal.ZERO;
        if (valorInicial.signum() < 0)
            throw new IllegalArgumentException("Suprimento inicial não pode ser negativo.");

        // Trava de segurança (mensalidade) — ver LICENCIAMENTO.md.
        // Bloqueia só abertura de NOVO movimento; um já aberto continua até fechar.
        if (!licencaService.podeAbrirCaixa(caixa)) {
            throw new IllegalStateException(
                    "Sistema bloqueado por pendência de pagamento. "
                            + "Entre em contato com o suporte para regularizar a mensalidade.");
        }

        movimentoRepository.findFirstByCaixaIdAndStatusMovimentoOrderByIdDesc(
                        caixa.getId(), StatusMovimento.A)
                .ifPresent(m -> {
                    throw new IllegalStateException(
                            "O caixa '" + caixa.getNome() + "' já possui um movimento aberto (ID " + m.getId() + ").");
                });

        movimentoRepository.findFirstByOperadorIdAndStatusMovimentoOrderByIdDesc(
                        operador.getId(), StatusMovimento.A)
                .ifPresent(m -> {
                    throw new IllegalStateException(
                            "O operador '" + operador.getColaborador().getNome() +
                                    "' já está em um movimento aberto (Caixa " + m.getCaixa().getNome() + ").");
                });

        PdvMovimento mov = new PdvMovimento();
        mov.setOperador(operador);
        mov.setCaixa(caixa);
        mov.setGerenteSupervisor(gerenteSupervisor);
        mov.setDataAbertura(LocalDate.now());
        mov.setHoraAbertura(LocalTime.now().format(HORA_FMT));

        mov.setValorFundoInicial(valorInicial);
        mov.setTotalSuprimento(java.math.BigDecimal.ZERO);

        mov.setTotalSangria(BigDecimal.ZERO);
        mov.setTotalNaoFiscal(BigDecimal.ZERO);
        mov.setTotalVenda(BigDecimal.ZERO);
        mov.setTotalDesconto(BigDecimal.ZERO);
        mov.setTotalAcrescimo(BigDecimal.ZERO);
        mov.setTotalFinal(BigDecimal.ZERO);
        mov.setTotalRecebido(BigDecimal.ZERO);
        mov.setTotalTroco(BigDecimal.ZERO);
        mov.setTotalCancelado(BigDecimal.ZERO);
        mov.setStatusMovimento(StatusMovimento.A);
        mov.setIdDispositivo(idDispositivo);

        PdvMovimento salvo = movimentoRepository.save(mov);
        LOGGER.info("Movimento aberto: ID={}, operador={}, caixa={}",
                salvo.getId(),
                salvo.getOperador().getColaborador().getNome(),
                salvo.getCaixa().getNome());
        return salvo;
    }

    /**
     * Troca o operador de um movimento em aberto. Exige autorização.
     */
    @Transactional
    public PdvMovimento trocarOperador(PdvMovimento movimento,
                                       PdvOperador novoOperador,
                                       PdvOperador gerenteSupervisor) {
        if (movimento.getStatusMovimento() != StatusMovimento.A) {
            throw new IllegalStateException("Movimento não está aberto.");
        }
        // Validação: novo operador não pode estar em outro movimento aberto
        movimentoRepository.findFirstByOperadorIdAndStatusMovimentoOrderByIdDesc(
                        novoOperador.getId(), StatusMovimento.A)
                .filter(m -> !m.getId().equals(movimento.getId()))
                .ifPresent(m -> {
                    throw new IllegalStateException(
                            "O operador '" + novoOperador.getColaborador().getNome() +
                                    "' já está em outro movimento aberto.");
                });

        PdvOperador anterior = movimento.getOperador();
        movimento.setOperador(novoOperador);
        movimento.setGerenteSupervisor(gerenteSupervisor);

        PdvMovimento salvo = movimentoRepository.save(movimento);
        LOGGER.info("Troca de operador no movimento {}: {} → {}",
                salvo.getId(),
                anterior.getColaborador().getNome(),
                novoOperador.getColaborador().getNome());
        return salvo;
    }

    /**
     * Retoma um movimento que estava com status T (Fechado Temporariamente).
     */
    @Transactional
    public PdvMovimento retomarMovimento(PdvMovimento movimento, PdvOperador operadorAtual) {
        if (movimento.getStatusMovimento() != StatusMovimento.T) {
            throw new IllegalStateException(
                    "Movimento não está em status T (Fechado Temporariamente).");
        }
        movimento.setStatusMovimento(StatusMovimento.A);
        // Se for o mesmo operador, só destrava. Se for outro, troca.
        if (!movimento.getOperador().getId().equals(operadorAtual.getId())) {
            movimento.setOperador(operadorAtual);
        }
        PdvMovimento salvo = movimentoRepository.save(movimento);
        LOGGER.info("Movimento {} retomado por {}", salvo.getId(),
                operadorAtual.getColaborador().getNome());
        return salvo;
    }

    /**
     * Fecha temporariamente o movimento (status A → T).
     *
     * <p>Usado pelos handlers F12 → Fechamento Temporário e F12 → Trocar
     * Operador. Em ambos os casos, o movimento fica preservado (vendas,
     * fundo, suprimentos, sangrias) e pode ser retomado depois via
     * {@link #retomarMovimento(PdvMovimento, PdvOperador)}.
     *
     * <p>Quando retomado:
     * <ul>
     *   <li>Pelo MESMO operador: continua a operação dele (uso típico:
     *       almoço, pausa rápida).</li>
     *   <li>Por OUTRO operador: o {@code retomarMovimento} já reatribui
     *       o operador automaticamente (uso típico: troca de turno).</li>
     * </ul>
     *
     * <p>Não altera saldos nem zera contadores — é apenas um "pause".
     *
     * @param movimento movimento em status A
     * @return movimento atualizado com status T
     */
    @Transactional
    public PdvMovimento fecharTemporariamente(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento não informado.");
        }
        if (movimento.getStatusMovimento() != StatusMovimento.A) {
            throw new IllegalStateException(
                    "Só é possível fechar temporariamente movimentos em status A (Aberto). "
                            + "Status atual: " + movimento.getStatusMovimento());
        }

        movimento.setStatusMovimento(StatusMovimento.T);

        PdvMovimento salvo = movimentoRepository.save(movimento);
        LOGGER.info("Movimento {} fechado temporariamente por {}",
                salvo.getId(),
                salvo.getOperador().getColaborador().getNome());
        return salvo;
    }
}