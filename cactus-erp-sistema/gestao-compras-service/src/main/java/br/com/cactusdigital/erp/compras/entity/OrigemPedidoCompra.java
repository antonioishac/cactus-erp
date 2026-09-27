package br.com.cactusdigital.erp.compras.entity;

/** De onde o pedido nasceu — mesmo idioma {@code origemTipo}/{@code origemId} já usado em
 * {@code EstoqueMovimento} no controle-estoque-service. {@code origemId} referencia o id
 * de {@link RequisicaoCompra} (REQUISICAO) ou {@link CotacaoCompra} (COTACAO); nulo em
 * AVULSO. O fenix.sql não obriga FK de {@code COMPRA_PEDIDO} pra requisição/cotação —
 * um pedido totalmente avulso é um cenário real, não uma lacuna do modelo. */
public enum OrigemPedidoCompra {
    REQUISICAO,
    COTACAO,
    AVULSO
}
