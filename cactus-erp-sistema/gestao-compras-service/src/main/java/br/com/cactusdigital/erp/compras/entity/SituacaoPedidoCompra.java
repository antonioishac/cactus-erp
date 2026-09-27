package br.com.cactusdigital.erp.compras.entity;

/**
 * {@code RASCUNHO} → (enviar) → {@code AGUARDANDO_APROVACAO} (só se acima do limite
 * configurado) ou direto {@code ENVIADO} → (recebimento parcial) →
 * {@code PARCIALMENTE_RECEBIDO} → (todos os itens recebidos) {@code RECEBIDO_TOTAL}.
 * {@code CANCELADO} alcançável de RASCUNHO/AGUARDANDO_APROVACAO/ENVIADO, nunca depois de
 * já ter recebido algo.
 */
public enum SituacaoPedidoCompra {
    RASCUNHO,
    AGUARDANDO_APROVACAO,
    ENVIADO,
    PARCIALMENTE_RECEBIDO,
    RECEBIDO_TOTAL,
    CANCELADO
}
