package br.com.cactuserp.pdv.service.dto;

import br.com.cactuserp.pdv.entity.PdvMovimento;

import java.math.BigDecimal;

/**
 * Snapshot dos totalizadores do movimento para exibição no relatório Z.
 *
 * <p>Construído pelo {@code FechamentoMovimentoService.consolidarRelatorioZ()}
 * e consumido pela tela {@code RelatorioZ.fxml}.
 *
 * <p>É um objeto <b>imutável</b> — gerado uma vez, consumido pela UI.
 * Não é entidade JPA; não persiste nada.
 *
 * <h3>Equações fiscais</h3>
 * <pre>
 *   Total Líquido = Total Vendas Bruto - Total Cancelado
 *   Saldo Esperado em Caixa = Fundo Inicial
 *                           + Total Vendas (Dinheiro)
 *                           + Total Suprimentos
 *                           - Total Sangrias
 *                           - Trocos pagos
 * </pre>
 *
 * <p>{@code vendasEmDinheiro} e {@code saldoEsperadoEmCaixa} vêm do
 * breakdown real por forma de pagamento
 * ({@code ConferenciaCaixaService#calcularVendasPorForma}/
 * {@code #calcularSaldoEsperadoDinheiro}), reaproveitado pelo
 * {@code FechamentoMovimentoService} — o mesmo cálculo usado na tela de
 * Conferência de Caixa, pra garantir que os dois nunca divirjam.
 */
public class RelatorioZ {

    // ====== Identificação do movimento ===================================
    private final Long idMovimento;
    private final String nomeOperador;
    private final String nomeCaixa;
    private final String dataAbertura;
    private final String horaAbertura;

    // ====== Vendas =======================================================
    private final BigDecimal totalVendasBruto;
    private final BigDecimal totalCancelado;
    private final BigDecimal totalVendasLiquido;

    /**
     * Vendas em dinheiro, valor bruto (sem ajuste de fundo/suprimento/
     * sangria/troco) — vem de {@code PDV_TOTAL_TIPO_PAGAMENTO} filtrando
     * a forma DINHEIRO. É uma métrica de exibição; para o valor que
     * deveria estar fisicamente na gaveta, ver {@link #saldoEsperadoEmCaixa}.
     */
    private final BigDecimal vendasEmDinheiro;

    // ====== Movimento de caixa ===========================================
    private final BigDecimal valorFundoInicial;
    private final BigDecimal totalSuprimento;
    private final BigDecimal totalSangria;

    // ====== Cálculos derivados ===========================================
    private final BigDecimal saldoEsperadoEmCaixa;

    public RelatorioZ(Long idMovimento, String nomeOperador, String nomeCaixa,
                      String dataAbertura, String horaAbertura,
                      BigDecimal totalVendasBruto, BigDecimal totalCancelado,
                      BigDecimal totalVendasLiquido, BigDecimal vendasEmDinheiro,
                      BigDecimal valorFundoInicial, BigDecimal totalSuprimento,
                      BigDecimal totalSangria, BigDecimal saldoEsperadoEmCaixa) {
        this.idMovimento = idMovimento;
        this.nomeOperador = nomeOperador;
        this.nomeCaixa = nomeCaixa;
        this.dataAbertura = dataAbertura;
        this.horaAbertura = horaAbertura;
        this.totalVendasBruto = totalVendasBruto;
        this.totalCancelado = totalCancelado;
        this.totalVendasLiquido = totalVendasLiquido;
        this.vendasEmDinheiro = vendasEmDinheiro;
        this.valorFundoInicial = valorFundoInicial;
        this.totalSuprimento = totalSuprimento;
        this.totalSangria = totalSangria;
        this.saldoEsperadoEmCaixa = saldoEsperadoEmCaixa;
    }

    /** Calcula a divergência dado um valor físico contado. */
    public BigDecimal calcularDivergencia(BigDecimal valorFisicoConferido) {
        if (valorFisicoConferido == null) return BigDecimal.ZERO;
        return valorFisicoConferido.subtract(saldoEsperadoEmCaixa);
    }

    public Long getIdMovimento() { return idMovimento; }
    public String getNomeOperador() { return nomeOperador; }
    public String getNomeCaixa() { return nomeCaixa; }
    public String getDataAbertura() { return dataAbertura; }
    public String getHoraAbertura() { return horaAbertura; }
    public BigDecimal getTotalVendasBruto() { return totalVendasBruto; }
    public BigDecimal getTotalCancelado() { return totalCancelado; }
    public BigDecimal getTotalVendasLiquido() { return totalVendasLiquido; }
    public BigDecimal getVendasEmDinheiro() { return vendasEmDinheiro; }
    public BigDecimal getValorFundoInicial() { return valorFundoInicial; }
    public BigDecimal getTotalSuprimento() { return totalSuprimento; }
    public BigDecimal getTotalSangria() { return totalSangria; }
    public BigDecimal getSaldoEsperadoEmCaixa() { return saldoEsperadoEmCaixa; }
}