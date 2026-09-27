package br.com.cactusdigital.erp.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FornecedorCotacaoItemRequest(
        @NotNull Long produtoId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade,
        @NotNull @DecimalMin(value = "0") BigDecimal valorUnitario,
        BigDecimal taxaDesconto) {
}
