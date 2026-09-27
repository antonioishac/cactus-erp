package br.com.cactusdigital.erp.loja.entity;

/**
 * {@code AGUARDANDO_PAGAMENTO} (nasce assim, ao finalizar o checkout) → {@code PAGO}
 * ({@code POST /pedidos/{id}/confirmar-pagamento} — ação manual/webhook-ready nesta
 * fatia, sem gateway real integrado ainda, ver CLAUDE.md) → {@code EM_SEPARACAO} →
 * {@code ENVIADO} → {@code ENTREGUE}. {@code CANCELADO} a partir de qualquer estado
 * anterior a {@code ENVIADO}.
 */
public enum SituacaoPedido {
    AGUARDANDO_PAGAMENTO,
    PAGO,
    EM_SEPARACAO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}
