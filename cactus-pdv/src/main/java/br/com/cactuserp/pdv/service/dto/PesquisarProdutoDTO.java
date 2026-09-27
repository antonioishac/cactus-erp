package br.com.cactuserp.pdv.service.dto;

import java.math.BigDecimal;

/**
 * Linha da tela de pesquisa de produto (F11, {@code PesquisaProdutoController}).
 *
 * <p>{@code codigo} é o que efetivamente volta pro campo de busca do PDV ao
 * selecionar a linha — precisa ser algo que {@code Pdv4Controller.addProduct}
 * saiba resolver (GTIN ou código de balança; ver
 * {@code ProdutoService#paraPesquisa}). Pode vir {@code null} no caso de
 * produto sem GTIN e sem código de balança cadastrados — se além disso o
 * produto não for vendido por peso, a tela desabilita a seleção da linha
 * (não tem como adicionar); se for vendido por peso, continua selecionável
 * mesmo sem código, porque {@code Pdv4Controller} pesa direto na balança
 * usando {@link #getId()}, sem precisar de código nenhum (ver
 * {@code Pdv4Controller.abrirPesquisaProduto}).
 */
public class PesquisarProdutoDTO {

    private final Long id;
    private final String codigo;
    private final String descricao;
    private final String unidade;
    private final BigDecimal precoVenda;
    private final boolean vendidoPorPeso;

    public PesquisarProdutoDTO(Long id, String codigo, String descricao, String unidade,
                                BigDecimal precoVenda, boolean vendidoPorPeso) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.unidade = unidade;
        this.precoVenda = precoVenda;
        this.vendidoPorPeso = vendidoPorPeso;
    }

    /**
     * ID do produto — permite recuperar o {@code Produto} completo depois
     * (ver {@code Pdv4Controller.abrirPesquisaProduto}), sem depender de um
     * código (GTIN/balança) que produto pesado sem etiqueta pode nem ter.
     */
    public Long getId() { return id; }

    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public String getUnidade() { return unidade; }
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public boolean isVendidoPorPeso() { return vendidoPorPeso; }

    /** {@code true} quando não há código nenhum resolvível — linha não deveria ser selecionável. */
    public boolean semCodigoResolvivel() {
        return codigo == null || codigo.isBlank();
    }
}
