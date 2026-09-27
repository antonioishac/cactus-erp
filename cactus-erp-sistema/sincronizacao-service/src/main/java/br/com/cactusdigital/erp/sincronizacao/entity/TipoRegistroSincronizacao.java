package br.com.cactusdigital.erp.sincronizacao.entity;

/**
 * Tipo de registro que o PDV empurra num envelope de lote ({@code POST /sync/lotes}).
 * Só {@link #ESTOQUE_MOVIMENTO} tem serviço de destino hoje (ver inventário da Fase 6,
 * seção 2.2 do FASE6_SINCRONIZACAO_PDV.md — 7 das 8 categorias de push não têm módulo de
 * Vendas/Caixa/Financeiro construído ainda). Os demais entram aqui quando esses módulos
 * existirem — não antes, pra não modelar destino que não existe.
 */
public enum TipoRegistroSincronizacao {
    ESTOQUE_MOVIMENTO
}
