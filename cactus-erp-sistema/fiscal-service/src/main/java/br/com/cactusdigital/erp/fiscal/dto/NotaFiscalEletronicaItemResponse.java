package br.com.cactusdigital.erp.fiscal.dto;

import java.math.BigDecimal;

public record NotaFiscalEletronicaItemResponse(
        Long id,
        Integer numeroItem,
        Long produtoId,
        String descricao,
        String gtin,
        String ncm,
        String cfop,
        String unidade,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        BigDecimal valorDesconto,
        String cstIcms,
        String csosn,
        BigDecimal aliquotaIcms,
        BigDecimal valorIcms,
        BigDecimal aliquotaIpi,
        BigDecimal valorIpi,
        BigDecimal aliquotaPis,
        BigDecimal valorPis,
        BigDecimal aliquotaCofins,
        BigDecimal valorCofins) {
}
