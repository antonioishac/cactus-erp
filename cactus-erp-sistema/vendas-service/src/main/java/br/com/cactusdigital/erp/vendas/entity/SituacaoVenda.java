package br.com.cactusdigital.erp.vendas.entity;

/**
 * {@code RASCUNHO} → (confirmar) → {@code CONFIRMADA} (gera {@code VendaComissao} e
 * {@code SAIDA} de estoque via {@code EstoqueClient}) → {@code CANCELADA} (só alcançável
 * a partir de {@code RASCUNHO} — depois de confirmada, o estoque/comissão já foi gerado;
 * fluxo de devolução fica pra quando a Fase 8/NF-e existir, ver Javadoc de
 * {@code VendaService#cancelar}).
 */
public enum SituacaoVenda {
    RASCUNHO,
    CONFIRMADA,
    CANCELADA
}
