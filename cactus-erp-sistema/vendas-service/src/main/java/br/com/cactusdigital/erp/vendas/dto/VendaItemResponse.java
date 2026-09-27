package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;

public record VendaItemResponse(
        Long id,
        Long produtoId,
        Long depositoId,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorSubtotal,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal) {
}
