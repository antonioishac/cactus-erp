package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Item lançado em uma conta aberta (mesa, comanda ou pedido).
 *
 * <p>Adota o mesmo padrão do {@link PdvVendaDetalhe}:
 * snapshot do produto (descrição/preço) preservado no lançamento,
 * cancelamento via flag (não remove fisicamente).
 */
@Entity
@Table(name = "CONTA_ABERTA_ITEM")
public class ContaAbertaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTA_ABERTA", nullable = false)
    private ContaAberta contaAberta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;

    @Column(name = "ITEM", nullable = false)
    private Integer item;

    @Column(name = "GTIN", length = 14)
    private String gtin;

    @Column(name = "DESCRICAO", length = 200, nullable = false)
    private String descricao;

    @Column(name = "QUANTIDADE", precision = 18, scale = 6, nullable = false)
    private BigDecimal quantidade;

    @Column(name = "VALOR_UNITARIO", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorUnitario;

    @Column(name = "VALOR_DESCONTO", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "VALOR_ACRESCIMO", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;

    @Column(name = "VALOR_TOTAL", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private SituacaoItemConta situacao = SituacaoItemConta.A;

    @Column(name = "OBSERVACAO", length = 255)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR_LANCAMENTO", nullable = false)
    private PdvOperador operadorLancamento;

    @Column(name = "DATA_LANCAMENTO", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "HORA_LANCAMENTO", length = 8, nullable = false)
    private String horaLancamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR_CANCELAMENTO")
    private PdvOperador operadorCancelamento;

    @Column(name = "DATA_CANCELAMENTO")
    private LocalDate dataCancelamento;

    @Column(name = "HORA_CANCELAMENTO", length = 8)
    private String horaCancelamento;

    @Column(name = "MOTIVO_CANCELAMENTO", length = 255)
    private String motivoCancelamento;

    public ContaAbertaItem() {}

    public boolean estaAtivo() { return situacao == SituacaoItemConta.A; }

    // ====== Getters / Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ContaAberta getContaAberta() { return contaAberta; }
    public void setContaAberta(ContaAberta v) { this.contaAberta = v; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getItem() { return item; }
    public void setItem(Integer item) { this.item = item; }

    public String getGtin() { return gtin; }
    public void setGtin(String gtin) { this.gtin = gtin; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal v) { this.quantidade = v; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal v) { this.valorUnitario = v; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal v) { this.valorDesconto = v; }

    public BigDecimal getValorAcrescimo() { return valorAcrescimo; }
    public void setValorAcrescimo(BigDecimal v) { this.valorAcrescimo = v; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal v) { this.valorTotal = v; }

    public SituacaoItemConta getSituacao() { return situacao; }
    public void setSituacao(SituacaoItemConta s) { this.situacao = s; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public PdvOperador getOperadorLancamento() { return operadorLancamento; }
    public void setOperadorLancamento(PdvOperador v) { this.operadorLancamento = v; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate v) { this.dataLancamento = v; }

    public String getHoraLancamento() { return horaLancamento; }
    public void setHoraLancamento(String v) { this.horaLancamento = v; }

    public PdvOperador getOperadorCancelamento() { return operadorCancelamento; }
    public void setOperadorCancelamento(PdvOperador v) { this.operadorCancelamento = v; }

    public LocalDate getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDate v) { this.dataCancelamento = v; }

    public String getHoraCancelamento() { return horaCancelamento; }
    public void setHoraCancelamento(String v) { this.horaCancelamento = v; }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String v) { this.motivoCancelamento = v; }
}