package br.com.cactuserp.pdv.service.dto;

import java.math.BigDecimal;

/**
 * Resultado do cálculo tributário de um item de venda — ver
 * {@code CalculoTributarioService}.
 *
 * <p>Objeto de transporte imutável (Single Responsibility: só carrega
 * dados, não tem comportamento). Os valores aqui são atribuídos 1:1 aos
 * campos correspondentes de {@code PdvVendaDetalhe}.
 */
public class ResultadoTributario {

    private final BigDecimal valorBaseIcms;
    private final BigDecimal taxaIcms;
    private final BigDecimal valorIcms;
    private final BigDecimal taxaPis;
    private final BigDecimal valorPis;
    private final BigDecimal taxaCofins;
    private final BigDecimal valorCofins;
    private final BigDecimal taxaIssqn;
    private final BigDecimal valorIssqn;

    public ResultadoTributario(BigDecimal valorBaseIcms, BigDecimal taxaIcms, BigDecimal valorIcms,
                               BigDecimal taxaPis, BigDecimal valorPis,
                               BigDecimal taxaCofins, BigDecimal valorCofins,
                               BigDecimal taxaIssqn, BigDecimal valorIssqn) {
        this.valorBaseIcms = valorBaseIcms;
        this.taxaIcms = taxaIcms;
        this.valorIcms = valorIcms;
        this.taxaPis = taxaPis;
        this.valorPis = valorPis;
        this.taxaCofins = taxaCofins;
        this.valorCofins = valorCofins;
        this.taxaIssqn = taxaIssqn;
        this.valorIssqn = valorIssqn;
    }

    public BigDecimal getValorBaseIcms() { return valorBaseIcms; }
    public BigDecimal getTaxaIcms() { return taxaIcms; }
    public BigDecimal getValorIcms() { return valorIcms; }
    public BigDecimal getTaxaPis() { return taxaPis; }
    public BigDecimal getValorPis() { return valorPis; }
    public BigDecimal getTaxaCofins() { return taxaCofins; }
    public BigDecimal getValorCofins() { return valorCofins; }
    public BigDecimal getTaxaIssqn() { return taxaIssqn; }
    public BigDecimal getValorIssqn() { return valorIssqn; }

    /** Soma ICMS + PIS + COFINS + ISSQN — usado pra conferência rápida. */
    public BigDecimal getValorTotalTributos() {
        return valorIcms.add(valorPis).add(valorCofins).add(valorIssqn);
    }
}
