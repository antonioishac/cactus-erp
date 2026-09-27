package br.com.cactusdigital.erp.fiscal.sped;

import java.math.BigDecimal;

/**
 * Uma linha agregada dos registros analíticos {@code C190}/{@code D190} da EFD ICMS/IPI —
 * agrupada por (CST/CSOSN, CFOP, alíquota), somando os valores de todos os itens/documentos
 * do período que caem nessa combinação. Tipado (não uma lista de strings) de propósito —
 * evita a classe de bug "ordem de campo trocada na hora de montar a linha", que é
 * exatamente o tipo de erro que "não podemos admitir" num export fiscal.
 */
record LinhaAnaliticaIcms(
        String cstOuCsosn,
        String cfop,
        BigDecimal aliquota,
        BigDecimal valorOperacao,
        BigDecimal baseCalculoIcms,
        BigDecimal valorIcms,
        BigDecimal valorIpi) {
}
