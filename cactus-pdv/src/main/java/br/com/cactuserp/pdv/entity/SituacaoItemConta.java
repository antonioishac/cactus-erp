package br.com.cactuserp.pdv.entity;

/**
 * Situação de um item lançado em uma conta aberta.
 *
 * <p>Cancelamento de item NÃO remove fisicamente do banco — mantém
 * pra auditoria (igual ao padrão do PDV_VENDA_DETALHE).
 */
public enum SituacaoItemConta {
    A,  // Ativo (item válido)
    C   // Cancelado (mantido pra auditoria)
}