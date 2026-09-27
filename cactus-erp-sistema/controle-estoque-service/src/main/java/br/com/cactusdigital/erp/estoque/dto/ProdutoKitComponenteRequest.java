package br.com.cactusdigital.erp.estoque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProdutoKitComponenteRequest(
        @NotNull Long produtoComponenteId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade) {
}
