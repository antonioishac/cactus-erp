package br.com.cactusdigital.erp.vendas.entity;

/**
 * VISTA/PRAZO — reaproveitado em 3 lugares (validado contra fenix.sql, todos {@code CHAR(1)}
 * V/P): {@code VendaCondicoesPagamento.vistaPrazo}, {@code ComissaoObjetivo.formaPagamento}
 * e {@code VendaComissao.tipoContabil}.
 */
public enum TipoPagamentoVenda {
    VISTA,
    PRAZO
}
