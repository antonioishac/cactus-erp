package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;

public record VendaOrcamentoItemResponse(
        Long id,
        Long produtoId,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorSubtotal,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal) {
}
