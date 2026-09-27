package br.com.cactusdigital.erp.financeiro.entity;

/** {@code VENDA} — gerada automaticamente por {@code vendas-service} ao confirmar uma venda (1 por parcela).
 * {@code PEDIDO_LOJA}/{@code MANUAL} registrados pra uso futuro/direto ({@code MANUAL} cobre avulso e
 * parcelado via {@code gerarParcelado}). {@code RENEGOCIACAO} (2026-09-06) — ver {@code OrigemContaPagar}. */
public enum OrigemContaReceber {
    VENDA,
    PEDIDO_LOJA,
    MANUAL,
    RENEGOCIACAO
}
