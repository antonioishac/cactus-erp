package br.com.cactusdigital.erp.compras.entity;

/** Validado contra {@code fenix.sql.COMPRA_TIPO_PEDIDO}: NORMAL = entrega única,
 * PLANEJADO = quantidade anual negociada com entregas mensais programadas, ABERTO =
 * quantidade não fechada, liberações geradas conforme necessidade. */
public enum TipoPedidoCompra {
    NORMAL,
    PLANEJADO,
    ABERTO
}
