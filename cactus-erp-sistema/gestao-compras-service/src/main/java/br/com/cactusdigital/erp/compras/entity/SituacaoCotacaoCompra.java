package br.com.cactusdigital.erp.compras.entity;

/** {@code FINALIZADA} é setado automaticamente quando um dos fornecedores concorrentes
 * tem um {@link PedidoCompra} gerado a partir dele — a cotação encerra o próprio ciclo. */
public enum SituacaoCotacaoCompra {
    ABERTA,
    FINALIZADA,
    CANCELADA
}
