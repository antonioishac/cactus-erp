package br.com.cactusdigital.erp.vendas.entity;

/**
 * {@code fenix.sql.VENDEDOR_META.PERIODO_META} é {@code CHAR(1)} sem domínio de valores
 * documentado na metadata do campo — inferência de mercado (não alucinação de dado
 * específico): granularidade usual de meta comercial em ERPs de varejo/atacado.
 */
public enum PeriodoMeta {
    DIARIO,
    SEMANAL,
    MENSAL,
    ANUAL
}
