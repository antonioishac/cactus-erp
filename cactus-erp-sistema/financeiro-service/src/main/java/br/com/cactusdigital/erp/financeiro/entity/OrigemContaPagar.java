package br.com.cactusdigital.erp.financeiro.entity;

/** {@code COMPRA} — gerada automaticamente pelo gestao-compras-service (1 por parcela) quando
 * um PedidoCompra vira RECEBIDO_TOTAL. {@code MANUAL} é criação direta pela API (avulsa ou
 * parcelada via {@code gerarParcelado}). {@code RENEGOCIACAO} (2026-09-06) — título novo
 * nascido de {@code renegociar()} sobre um título original marcado {@code SituacaoTitulo#RENEGOCIADA}. */
public enum OrigemContaPagar {
    COMPRA,
    MANUAL,
    RENEGOCIACAO
}
