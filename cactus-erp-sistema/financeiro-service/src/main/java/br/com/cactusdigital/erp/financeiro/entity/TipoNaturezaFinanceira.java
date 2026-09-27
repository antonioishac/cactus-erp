package br.com.cactusdigital.erp.financeiro.entity;

/** Espelha {@code FIN_NATUREZA_FINANCEIRA.TIPO} do {@code fenix.sql} (CHAR(1), 'R'/'D' — inferido do
 * próprio comentário da tabela, que descreve o padrão "100N para Receita, 200N para Despesa"). */
public enum TipoNaturezaFinanceira {
    RECEITA,
    DESPESA
}
