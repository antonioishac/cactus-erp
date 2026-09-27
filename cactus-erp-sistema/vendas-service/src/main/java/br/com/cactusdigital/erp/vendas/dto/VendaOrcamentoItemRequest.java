package br.com.cactusdigital.erp.vendas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VendaOrcamentoItemRequest(
        @NotNull Long produtoId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade,
        @NotNull @DecimalMin(value = "0") BigDecimal valorUnitario,
        BigDecimal taxaDesconto) {
}
