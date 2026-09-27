package br.com.cactusdigital.erp.compras.entity;

/** {@code PEDIDO_GERADO} é setado automaticamente quando um {@link PedidoCompra} nasce
 * direto desta requisição ou de uma {@link CotacaoCompra} vinculada a ela. */
public enum SituacaoRequisicaoCompra {
    ABERTA,
    PEDIDO_GERADO,
    CANCELADA
}
