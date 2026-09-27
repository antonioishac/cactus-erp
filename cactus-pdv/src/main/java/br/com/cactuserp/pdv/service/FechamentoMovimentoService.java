package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.StatusMovimento;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.repository.PdvMovimentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.service.dto.RelatorioZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Serviço responsável pelo fechamento de movimento de caixa (relatório Z).
 *
 * <p>Duas operações principais:
 * <ul>
 *   <li>{@link #consolidarRelatorioZ}: consolida os totalizadores em
 *       memória, sem persistir. Usado pela UI para exibir antes da
 *       confirmação.</li>
 *   <li>{@link #fecharMovimento}: persiste o fechamento — status F,
 *       valor físico conferido, divergência, data/hora, gerente
 *       autorizador. Bloqueia se houver cupom em aberto.</li>
 * </ul>
 *
 * <h3>Cupom em aberto: regra fiscal</h3>
 * Não permitimos fechar movimento com cupom {@code STATUS_VENDA='A'} no
 * banco. Isso geraria cupom órfão (cliente sem atendimento concluído) e
 * causa alerta na auditoria PAF-NFC-e. O operador deve resolver o cupom
 * (F2 cancela ou F7 finaliza) antes de fechar.
 */
@Service
@Transactional(readOnly = true)
public class FechamentoMovimentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FechamentoMovimentoService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PdvMovimentoRepository movimentoRepository;
    private final PdvVendaCabecalhoRepository vendaRepository;
    private final SessaoPdv sessaoPdv;
    private final ConferenciaCaixaService conferenciaCaixaService;

    public FechamentoMovimentoService(PdvMovimentoRepository movimentoRepository,
                                      PdvVendaCabecalhoRepository vendaRepository,
                                      SessaoPdv sessaoPdv,
                                      ConferenciaCaixaService conferenciaCaixaService) {
        this.movimentoRepository = movimentoRepository;
        this.vendaRepository = vendaRepository;
        this.sessaoPdv = sessaoPdv;
        this.conferenciaCaixaService = conferenciaCaixaService;
    }

    // ====================================================================
    //  CONSULTAS — consolidar relatório Z (sem persistir)
    // ====================================================================

    /**
     * Consolida os totalizadores do movimento em um snapshot legível.
     * Não persiste nada — apenas calcula. Pode ser chamado várias vezes.
     *
     * <p>{@code vendasEmDinheiro} e {@code saldoEsperadoEmCaixa} reaproveitam
     * {@link ConferenciaCaixaService}, que já calcula o breakdown real por
     * forma de pagamento (a partir de {@code PDV_TOTAL_TIPO_PAGAMENTO}) —
     * isso garante que o Relatório Z e a Conferência de Caixa nunca
     * divirjam sobre quanto dinheiro deveria estar na gaveta.
     */
    public RelatorioZ consolidarRelatorioZ(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento é obrigatório.");
        }

        // Recarrega para garantir estado atual (sem proxies stale)
        PdvMovimento mov = movimentoRepository.findById(movimento.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Movimento #" + movimento.getId() + " não encontrado."));

        BigDecimal totalBruto = vendaRepository.somarTotalVendasFechadas(mov)
                .orElse(BigDecimal.ZERO);
        BigDecimal totalCancelado = vendaRepository.somarTotalCancelado(mov)
                .orElse(BigDecimal.ZERO);
        BigDecimal totalLiquido = totalBruto.subtract(totalCancelado);
        if (totalLiquido.compareTo(BigDecimal.ZERO) < 0) {
            totalLiquido = BigDecimal.ZERO;
        }

        // Vendas em dinheiro (bruto, sem ajuste de fundo/suprimento/sangria/
        // troco) — é uma métrica de exibição, não o saldo esperado na gaveta.
        BigDecimal vendasEmDinheiro = conferenciaCaixaService.calcularVendasPorForma(mov)
                .entrySet().stream()
                .filter(e -> ConferenciaCaixaService.CODIGO_DINHEIRO.equals(e.getKey().getCodigo()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal fundoInicial = nullSafe(mov.getValorFundoInicial());
        BigDecimal totalSup = nullSafe(mov.getTotalSuprimento());
        BigDecimal totalSan = nullSafe(mov.getTotalSangria());

        // Saldo esperado real na gaveta — já inclui fundo + suprimento -
        // sangria - troco (ver ConferenciaCaixaService.calcularEsperadoPorForma).
        BigDecimal saldoEsperado = conferenciaCaixaService.calcularSaldoEsperadoDinheiro(mov);

        String nomeOperador = mov.getOperador() != null
                ? mov.getOperador().getColaborador().getNome()
                : "—";
        String nomeCaixa = mov.getCaixa() != null ? mov.getCaixa().getNome() : "—";
        String dataAbertura = mov.getDataAbertura() != null
                ? mov.getDataAbertura().format(FMT_DATA) : "—";
        String horaAbertura = mov.getHoraAbertura() != null ? mov.getHoraAbertura() : "—";

        return new RelatorioZ(
                mov.getId(), nomeOperador, nomeCaixa,
                dataAbertura, horaAbertura,
                totalBruto, totalCancelado, totalLiquido, vendasEmDinheiro,
                fundoInicial, totalSup, totalSan,
                saldoEsperado
        );
    }

    /**
     * Verifica se há cupom aberto (status A) no movimento informado.
     * Usado pela UI para bloquear o fechamento.
     */
    public boolean temCupomAberto(PdvMovimento movimento) {
        if (movimento == null) return false;
        return vendaRepository.countByPdvMovimentoAndStatusVenda(
                movimento, StatusVenda.A) > 0;
    }

    // ====================================================================
    //  ESCRITA — fechar movimento
    // ====================================================================

    /**
     * Fecha definitivamente o movimento. Operação atômica.
     *
     * @param movimento             movimento a fechar
     * @param valorFisicoConferido  valor em dinheiro contado pelo operador
     * @param gerenteAutorizador    gerente/supervisor que autorizou (obrigatório)
     * @return o movimento atualizado (com status F)
     * @throws IllegalStateException se houver cupom em aberto ou movimento
     *                               já estiver fechado
     */
    @Transactional
    public PdvMovimento fecharMovimento(PdvMovimento movimento,
                                        BigDecimal valorFisicoConferido,
                                        PdvOperador gerenteAutorizador) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento é obrigatório.");
        }
        if (valorFisicoConferido == null || valorFisicoConferido.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Valor físico conferido é obrigatório e não pode ser negativo.");
        }
        if (gerenteAutorizador == null) {
            throw new IllegalArgumentException(
                    "Autorização gerencial é obrigatória para fechar movimento.");
        }

        // Recarrega na sessão atual
        PdvMovimento mov = movimentoRepository.findById(movimento.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Movimento #" + movimento.getId() + " não encontrado."));

        if (mov.getStatusMovimento() != StatusMovimento.A) {
            throw new IllegalStateException(
                    "Movimento não está aberto. Status atual: " + mov.getStatusMovimento());
        }

        // Regra fiscal: bloqueia se há cupom A
        if (temCupomAberto(mov)) {
            throw new IllegalStateException(
                    "Existe cupom em aberto neste movimento. Retome ou cancele o cupom "
                            + "antes de fechar o caixa.");
        }

        // Consolida e grava os totais
        RelatorioZ z = consolidarRelatorioZ(mov);

        mov.setTotalVenda(z.getTotalVendasLiquido());
        mov.setTotalCancelado(z.getTotalCancelado());
        mov.setTotalFinal(z.getSaldoEsperadoEmCaixa());
        mov.setValorFisicoConferido(valorFisicoConferido);
        mov.setValorDivergencia(z.calcularDivergencia(valorFisicoConferido));
        mov.setGerenteFechamento(gerenteAutorizador);
        mov.setDataFechamento(LocalDate.now());
        mov.setHoraFechamento(LocalTime.now().format(FMT_HORA));
        mov.setStatusMovimento(StatusMovimento.F);

        mov = movimentoRepository.save(mov);

        // Atualiza a sessão em memória — outras telas verão o novo status
        sessaoPdv.atualizarMovimento(mov);

        LOGGER.info("Movimento fechado: ID={}, saldoEsperado={}, fisico={}, divergencia={}, autorizador={}",
                mov.getId(),
                z.getSaldoEsperadoEmCaixa(),
                valorFisicoConferido,
                mov.getValorDivergencia(),
                gerenteAutorizador.getId());

        return mov;
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    private BigDecimal nullSafe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}