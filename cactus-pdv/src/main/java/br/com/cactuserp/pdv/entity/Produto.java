package br.com.cactuserp.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ID_PRODUTO_UNIDADE")
    private Long produtoUnidade;

    @Column(name = "ID_TRIBUT_GRUPO_TRIBUTARIO")
    private Long grupoTributario;

    @Column(name = "ID_PRODUTO_TIPO")
    private Long produtoTipo;

    @Column(name = "ID_PRODUTO_SUBGRUPO")
    private Long produtoSubgrupo;

    @Column(name = "GTIN", length = 14)
    private String gtin;

    @Column(name = "CODIGO_INTERNO", length = 50)
    private String codigoInterno;

    @Column(name = "NOME", length = 100)
    private String nome;

    @Column(name = "DESCRICAO", length = 250)
    private String descricao;

    @Column(name = "DESCRICAO_PDV", length = 30)
    private String descricaoPdv;

    @Column(name = "VALOR_COMPRA")
    private BigDecimal valorCompra;

    @Column(name = "VALOR_VENDA")
    private BigDecimal valorVenda;

    @Column(name = "QUANTIDADE_ESTOQUE")
    private BigDecimal quantidadeEstoque;

    @Column(name = "ESTOQUE_MINIMO")
    private BigDecimal estoqueMinimo;

    @Column(name = "ESTOQUE_MAXIMO")
    private BigDecimal estoqueMaximo;

    @Column(name = "CODIGO_NCM", length = 8)
    private String codigoNcm;

    // A=ARREDONDAMENTO | T=TRUNCAMENTO
    @Column(name = "IAT", length = 1)
    private String iat;

    // P=PROPRIO | T=TERCEIRO
    @Column(name = "IPPT", length = 1)
    private String ippt;

    @Column(name = "TIPO_ITEM_SPED", length = 2)
    private String tipoItemSped;

    @Column(name = "TAXA_IPI")
    private BigDecimal taxaIpi;

    @Column(name = "TAXA_ISSQN")
    private BigDecimal taxaIssqn;

    @Column(name = "TAXA_PIS")
    private BigDecimal taxaPis;

    @Column(name = "TAXA_COFINS")
    private BigDecimal taxaCofins;

    @Column(name = "TAXA_ICMS")
    private BigDecimal taxaIcms;

    @Column(name = "CST", length = 3)
    private String cst;

    @Column(name = "CSOSN", length = 4)
    private String csosn;

    @Column(name = "TOTALIZADOR_PARCIAL", length = 10)
    private String totalizadorParcial;

    @Column(name = "CODIGO_BALANCA")
    private Integer codigoBalanca;

    @Column(name = "PAF_P_ST", length = 1)
    private String pafPst;

    @Column(name = "HASH_REGISTRO", length = 32)
    private String hashRegistro;

    @Column(name = "VALOR_CUSTO")
    private BigDecimal valorCusto;

    // A=ATIVO I=INATIVO
    @Column(name = "SITUACAO", length = 1)
    private String situacao;

    @Column(name = "CODIGO_CEST", length = 7)
    private String codigoCest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoUnidade() {
        return produtoUnidade;
    }

    public void setProdutoUnidade(Long produtoUnidade) {
        this.produtoUnidade = produtoUnidade;
    }

    public Long getGrupoTributario() {
        return grupoTributario;
    }

    public void setGrupoTributario(Long grupoTributario) {
        this.grupoTributario = grupoTributario;
    }

    public Long getProdutoTipo() {
        return produtoTipo;
    }

    public void setProdutoTipo(Long produtoTipo) {
        this.produtoTipo = produtoTipo;
    }

    public Long getProdutoSubgrupo() {
        return produtoSubgrupo;
    }

    public void setProdutoSubgrupo(Long produtoSubgrupo) {
        this.produtoSubgrupo = produtoSubgrupo;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoPdv() {
        return descricaoPdv;
    }

    public void setDescricaoPdv(String descricaoPdv) {
        this.descricaoPdv = descricaoPdv;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public BigDecimal getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(BigDecimal estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public BigDecimal getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public void setEstoqueMaximo(BigDecimal estoqueMaximo) {
        this.estoqueMaximo = estoqueMaximo;
    }

    public String getCodigoNcm() {
        return codigoNcm;
    }

    public void setCodigoNcm(String codigoNcm) {
        this.codigoNcm = codigoNcm;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getIppt() {
        return ippt;
    }

    public void setIppt(String ippt) {
        this.ippt = ippt;
    }

    public String getTipoItemSped() {
        return tipoItemSped;
    }

    public void setTipoItemSped(String tipoItemSped) {
        this.tipoItemSped = tipoItemSped;
    }

    public BigDecimal getTaxaIpi() {
        return taxaIpi;
    }

    public void setTaxaIpi(BigDecimal taxaIpi) {
        this.taxaIpi = taxaIpi;
    }

    public BigDecimal getTaxaIssqn() {
        return taxaIssqn;
    }

    public void setTaxaIssqn(BigDecimal taxaIssqn) {
        this.taxaIssqn = taxaIssqn;
    }

    public BigDecimal getTaxaPis() {
        return taxaPis;
    }

    public void setTaxaPis(BigDecimal taxaPis) {
        this.taxaPis = taxaPis;
    }

    public BigDecimal getTaxaCofins() {
        return taxaCofins;
    }

    public void setTaxaCofins(BigDecimal taxaCofins) {
        this.taxaCofins = taxaCofins;
    }

    public BigDecimal getTaxaIcms() {
        return taxaIcms;
    }

    public void setTaxaIcms(BigDecimal taxaIcms) {
        this.taxaIcms = taxaIcms;
    }

    public String getCst() {
        return cst;
    }

    public void setCst(String cst) {
        this.cst = cst;
    }

    public String getCsosn() {
        return csosn;
    }

    public void setCsosn(String csosn) {
        this.csosn = csosn;
    }

    public String getTotalizadorParcial() {
        return totalizadorParcial;
    }

    public void setTotalizadorParcial(String totalizadorParcial) {
        this.totalizadorParcial = totalizadorParcial;
    }

    public Integer getCodigoBalanca() {
        return codigoBalanca;
    }

    public void setCodigoBalanca(Integer codigoBalanca) {
        this.codigoBalanca = codigoBalanca;
    }

    public String getPafPst() {
        return pafPst;
    }

    public void setPafPst(String pafPst) {
        this.pafPst = pafPst;
    }

    public String getHashRegistro() {
        return hashRegistro;
    }

    public void setHashRegistro(String hashRegistro) {
        this.hashRegistro = hashRegistro;
    }

    public BigDecimal getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(BigDecimal valorCusto) {
        this.valorCusto = valorCusto;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getCodigoCest() {
        return codigoCest;
    }

    public void setCodigoCest(String codigoCest) {
        this.codigoCest = codigoCest;
    }
}
