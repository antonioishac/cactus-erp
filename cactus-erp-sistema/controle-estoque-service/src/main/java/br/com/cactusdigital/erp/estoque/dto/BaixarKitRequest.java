package br.com.cactusdigital.erp.estoque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BaixarKitRequest(
        @NotNull Long depositoId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade) {
}
