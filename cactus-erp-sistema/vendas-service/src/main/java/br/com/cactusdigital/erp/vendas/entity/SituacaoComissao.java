package br.com.cactusdigital.erp.vendas.entity;

/** Ciclo de vida do lançamento de comissão — gerado {@code PENDENTE} na confirmação da
 * venda; {@code PAGO}/{@code CANCELADO} ficam pro futuro módulo Financeiro liquidar. */
public enum SituacaoComissao {
    PENDENTE,
    PAGO,
    CANCELADO
}
