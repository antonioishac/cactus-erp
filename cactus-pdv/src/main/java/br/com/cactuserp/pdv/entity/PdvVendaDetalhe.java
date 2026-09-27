package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Item (linha) de uma venda no PDV.
 *
 * <p>Mapeia a tabela {@code PDV_VENDA_DETALHE}. Cada bip de produto na tela
 * gera um registro desta entidade, sempre vinculado a um {@link PdvVendaCabecalho}.
 *
 * <p>Itens cancelados <b>nunca</b> são removidos fisicamente — apenas marcados
 * com {@link #cancelado} = {@link SimNao#S}. Isso preserva a auditoria fiscal
 * (PAF-NFC-e exige rastro de todas as operações).
 */
@Entity
@Table(name = "PDV_VENDA_DETALHE")
public class PdvVendaDetalhe {

    // ====== Identificação ====================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ====== Relacionamentos ==================================================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PDV_VENDA_CABECALHO", nullable = false)
    private PdvVendaCabecalho pdvVendaCabecalho;

    // ====== Identificação fiscal do item =====================================
    @Column(name = "CFOP")
    private Integer cfop;

    @Column(name = "GTIN", length = 14)
    private String gtin;

    @Column(name = "CCF")
    private Integer ccf;

    @Column(name = "COO")
    private Integer coo;

    @Column(name = "ITEM")
    private Integer item; // número sequencial do item no cupom

    @Column(name = "VERSAO", nullable = false)
    private Integer versao;

    // ====== Quantidade e valores =============================================
    @Column(name = "QUANTIDADE", precision = 18, scale = 6)
    private BigDecimal quantidade;

    @Column(name = "VALOR_UNITARIO", precision = 18, scale = 6)
    private BigDecimal valorUnitario;

    @Column(name = "VALOR_TOTAL", precision = 18, scale = 6)
    private BigDecimal valorTotal;

    @Column(name = "VALOR_TOTAL_ITEM", precision = 18, scale = 6)
    private BigDecimal valorTotalItem;

    // ====== ICMS =============================================================
    @Column(name = "VALOR_BASE_ICMS", precision = 18, scale = 6)
    private BigDecimal valorBaseIcms;

    @Column(name = "TAXA_ICMS", precision = 18, scale = 6)
    private BigDecimal taxaIcms;

    @Column(name = "VALOR_ICMS", precision = 18, scale = 6)
    private BigDecimal valorIcms;

    // ====== Descontos / Acréscimos ===========================================
    @Column(name = "TAXA_DESCONTO", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "VALOR_DESCONTO", precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "TAXA_ACRESCIMO", precision = 18, scale = 6)
    private BigDecimal taxaAcrescimo;

    @Column(name = "VALOR_ACRESCIMO", precision = 18, scale = 6)
    private BigDecimal valorAcrescimo;

    @Column(name = "VALOR_DESCONTO_ITEM", precision = 18, scale = 6)
    private BigDecimal valorDescontoItem;

    @Column(name = "VALOR_DESCONTO_RATEIO", precision = 18, scale = 6)
    private BigDecimal valorDescontoRateio;

    @Column(name = "VALOR_ACRESCIMO_ITEM", precision = 18, scale = 6)
    private BigDecimal valorAcrescimoItem;

    @Column(name = "VALOR_ACRESCIMO_RATEIO", precision = 18, scale = 6)
    private BigDecimal valorAcrescimoRateio;

    // ====== ISSQN ============================================================
    @Column(name = "TAXA_ISSQN", precision = 18, scale = 6)
    private BigDecimal taxaIssqn;

    @Column(name = "VALOR_ISSQN", precision = 18, scale = 6)
    private BigDecimal valorIssqn;

    // ====== PIS / COFINS =====================================================
    @Column(name = "TAXA_PIS", precision = 18, scale = 6)
    private BigDecimal taxaPis;

    @Column(name = "VALOR_PIS", precision = 18, scale = 6)
    private BigDecimal valorPis;

    @Column(name = "TAXA_COFINS", precision = 18, scale = 6)
    private BigDecimal taxaCofins;

    @Column(name = "VALOR_COFINS", precision = 18, scale = 6)
    private BigDecimal valorCofins;

    // ====== Impostos consolidados (Lei da Transparência) =====================
    @Column(name = "VALOR_IMPOSTO_FEDERAL", precision = 18, scale = 6)
    private BigDecimal valorImpostoFederal;

    @Column(name = "VALOR_IMPOSTO_ESTADUAL", precision = 18, scale = 6)
    private BigDecimal valorImpostoEstadual;

    @Column(name = "VALOR_IMPOSTO_MUNICIPAL", precision = 18, scale = 6)
    private BigDecimal valorImpostoMunicipal;

    // ====== Classificação fiscal =============================================
    @Column(name = "TOTALIZADOR_PARCIAL", length = 10)
    private String totalizadorParcial;

    @Column(name = "CST", length = 3, columnDefinition = "CHAR(3)")
    private String cst;

    @Column(name = "ECF_ICMS_ST", length = 4)
    private String ecfIcmsSt;

    // ====== Flags ============================================================
    @Enumerated(EnumType.STRING)
    @Column(name = "CANCELADO", length = 1, columnDefinition = "CHAR(1)")
    private SimNao cancelado;

    @Enumerated(EnumType.STRING)
    @Column(name = "MOVIMENTA_ESTOQUE", length = 1, columnDefinition = "CHAR(1)")
    private SimNao movimentaEstoque;

    // ====== Auditoria fiscal (PAF-NFC-e) ====================================
    @Column(name = "HASH_REGISTRO", length = 64)
    private String hashRegistro;

    @Column(name = "ID_GERADO_DISPOSITIVO")
    private Integer idGeradoDispositivo;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    // ====== Rastreabilidade — item de origem (Fase 1.2) ====================
    /**
     * Vínculo com o item original na conta aberta (quando aplicável).
     * NULL quando item foi bipado direto no caixa.
     * Permite distinguir itens lançados pelo garçom dos bipados pelo caixa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTA_ABERTA_ITEM")
    private ContaAbertaItem contaAbertaItem;

    // ====== Construtores =====================================================
    public PdvVendaDetalhe() {}

    // ====== Helpers de negócio ==============================================
    /** Atalho — true quando o item foi cancelado pelo operador. */
    public boolean estaCancelado() {
        return cancelado == SimNao.S;
    }

    // ====== Getters / Setters ===============================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public PdvVendaCabecalho getPdvVendaCabecalho() { return pdvVendaCabecalho; }
    public void setPdvVendaCabecalho(PdvVendaCabecalho c) { this.pdvVendaCabecalho = c; }

    public Integer getCfop() { return cfop; }
    public void setCfop(Integer cfop) { this.cfop = cfop; }

    public String getGtin() { return gtin; }
    public void setGtin(String gtin) { this.gtin = gtin; }

    public Integer getCcf() { return ccf; }
    public void setCcf(Integer ccf) { this.ccf = ccf; }

    public Integer getCoo() { return coo; }
    public void setCoo(Integer coo) { this.coo = coo; }

    public Integer getItem() { return item; }
    public void setItem(Integer item) { this.item = item; }

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal v) { this.valorUnitario = v; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal v) { this.valorTotal = v; }

    public BigDecimal getValorTotalItem() { return valorTotalItem; }
    public void setValorTotalItem(BigDecimal v) { this.valorTotalItem = v; }

    public BigDecimal getValorBaseIcms() { return valorBaseIcms; }
    public void setValorBaseIcms(BigDecimal v) { this.valorBaseIcms = v; }

    public BigDecimal getTaxaIcms() { return taxaIcms; }
    public void setTaxaIcms(BigDecimal v) { this.taxaIcms = v; }

    public BigDecimal getValorIcms() { return valorIcms; }
    public void setValorIcms(BigDecimal v) { this.valorIcms = v; }

    public BigDecimal getTaxaDesconto() { return taxaDesconto; }
    public void setTaxaDesconto(BigDecimal v) { this.taxaDesconto = v; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal v) { this.valorDesconto = v; }

    public BigDecimal getTaxaAcrescimo() { return taxaAcrescimo; }
    public void setTaxaAcrescimo(BigDecimal v) { this.taxaAcrescimo = v; }

    public BigDecimal getValorAcrescimo() { return valorAcrescimo; }
    public void setValorAcrescimo(BigDecimal v) { this.valorAcrescimo = v; }

    public BigDecimal getValorDescontoItem() { return valorDescontoItem; }
    public void setValorDescontoItem(BigDecimal v) { this.valorDescontoItem = v; }

    public BigDecimal getValorDescontoRateio() { return valorDescontoRateio; }
    public void setValorDescontoRateio(BigDecimal v) { this.valorDescontoRateio = v; }

    public BigDecimal getValorAcrescimoItem() { return valorAcrescimoItem; }
    public void setValorAcrescimoItem(BigDecimal v) { this.valorAcrescimoItem = v; }

    public BigDecimal getValorAcrescimoRateio() { return valorAcrescimoRateio; }
    public void setValorAcrescimoRateio(BigDecimal v) { this.valorAcrescimoRateio = v; }

    public BigDecimal getTaxaIssqn() { return taxaIssqn; }
    public void setTaxaIssqn(BigDecimal v) { this.taxaIssqn = v; }

    public BigDecimal getValorIssqn() { return valorIssqn; }
    public void setValorIssqn(BigDecimal v) { this.valorIssqn = v; }

    public BigDecimal getTaxaPis() { return taxaPis; }
    public void setTaxaPis(BigDecimal v) { this.taxaPis = v; }

    public BigDecimal getValorPis() { return valorPis; }
    public void setValorPis(BigDecimal v) { this.valorPis = v; }

    public BigDecimal getTaxaCofins() { return taxaCofins; }
    public void setTaxaCofins(BigDecimal v) { this.taxaCofins = v; }

    public BigDecimal getValorCofins() { return valorCofins; }
    public void setValorCofins(BigDecimal v) { this.valorCofins = v; }

    public BigDecimal getValorImpostoFederal() { return valorImpostoFederal; }
    public void setValorImpostoFederal(BigDecimal v) { this.valorImpostoFederal = v; }

    public BigDecimal getValorImpostoEstadual() { return valorImpostoEstadual; }
    public void setValorImpostoEstadual(BigDecimal v) { this.valorImpostoEstadual = v; }

    public BigDecimal getValorImpostoMunicipal() { return valorImpostoMunicipal; }
    public void setValorImpostoMunicipal(BigDecimal v) { this.valorImpostoMunicipal = v; }

    public String getTotalizadorParcial() { return totalizadorParcial; }
    public void setTotalizadorParcial(String s) { this.totalizadorParcial = s; }

    public String getCst() { return cst; }
    public void setCst(String cst) { this.cst = cst; }

    public String getEcfIcmsSt() { return ecfIcmsSt; }
    public void setEcfIcmsSt(String s) { this.ecfIcmsSt = s; }

    public SimNao getCancelado() { return cancelado; }
    public void setCancelado(SimNao cancelado) { this.cancelado = cancelado; }

    public SimNao getMovimentaEstoque() { return movimentaEstoque; }
    public void setMovimentaEstoque(SimNao v) { this.movimentaEstoque = v; }

    public String getHashRegistro() { return hashRegistro; }
    public void setHashRegistro(String hashRegistro) { this.hashRegistro = hashRegistro; }

    public Integer getIdGeradoDispositivo() { return idGeradoDispositivo; }
    public void setIdGeradoDispositivo(Integer v) { this.idGeradoDispositivo = v; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String idDispositivo) { this.idDispositivo = idDispositivo; }

    public ContaAbertaItem getContaAbertaItem() { return contaAbertaItem; }
    public void setContaAbertaItem(ContaAbertaItem item) { this.contaAbertaItem = item; }
}