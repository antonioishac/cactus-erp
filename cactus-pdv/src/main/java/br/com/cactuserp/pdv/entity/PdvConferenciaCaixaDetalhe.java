package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Detalhe de uma conferência de caixa — uma linha por forma de pagamento.
 */
@Entity
@Table(name = "PDV_CONFERENCIA_CAIXA_DETALHE")
public class PdvConferenciaCaixaDetalhe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_CONFERENCIA_CAIXA", nullable = false)
    private PdvConferenciaCaixa conferenciaCaixa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PDV_TIPO_PAGAMENTO", nullable = false)
    private TipoPagamento tipoPagamento;

    @Column(name = "VALOR_ESPERADO", precision = 18, scale = 6)
    private BigDecimal valorEsperado;

    @Column(name = "VALOR_CONTADO", precision = 18, scale = 6)
    private BigDecimal valorContado;

    @Column(name = "VALOR_DIVERGENCIA", precision = 18, scale = 6)
    private BigDecimal valorDivergencia;

    public PdvConferenciaCaixaDetalhe() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvConferenciaCaixa getConferenciaCaixa() { return conferenciaCaixa; }
    public void setConferenciaCaixa(PdvConferenciaCaixa v) { this.conferenciaCaixa = v; }

    public TipoPagamento getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(TipoPagamento v) { this.tipoPagamento = v; }

    public BigDecimal getValorEsperado() { return valorEsperado; }
    public void setValorEsperado(BigDecimal v) { this.valorEsperado = v; }

    public BigDecimal getValorContado() { return valorContado; }
    public void setValorContado(BigDecimal v) { this.valorContado = v; }

    public BigDecimal getValorDivergencia() { return valorDivergencia; }
    public void setValorDivergencia(BigDecimal v) { this.valorDivergencia = v; }
}