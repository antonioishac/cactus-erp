package br.com.cactusdigital.erp.vendas.entity;

/**
 * Melhoria sobre o {@code fenix.sql.VENDA_ORCAMENTO_CABECALHO}, que não tem status
 * explícito (o fenix infere se o orçamento virou venda só pela existência ou não de uma
 * {@code VENDA_CABECALHO} referenciando o orçamento) — mercado real (TOTVS/SAP B1)
 * rastreia o ciclo de vida do orçamento explicitamente. Máquina de estados em
 * {@code VendaOrcamentoService}.
 */
public enum SituacaoOrcamento {
    RASCUNHO,
    ENVIADO,
    APROVADO,
    CONVERTIDO,
    EXPIRADO,
    CANCELADO
}
