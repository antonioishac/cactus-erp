package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;

public record VendaPdvItemResponse(
        Long id,
        Integer numeroItem,
        Long produtoId,
        String gtin,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorDesconto,
        BigDecimal valorAcrescimo,
        BigDecimal valorTotal,
        Integer cfop,
        String cst,
        BigDecimal aliquotaIcms,
        BigDecimal valorIcms,
        BigDecimal valorPis,
        BigDecimal valorCofins,
        boolean cancelado,
        BigDecimal quantidadeDevolvida) {
}
