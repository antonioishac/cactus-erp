package br.com.cactusdigital.erp.estoque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record TransferenciaEstoqueRequest(
        @NotNull Long produtoId,
        @NotNull Long depositoOrigemId,
        @NotNull Long depositoDestinoId,
        Long loteId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade,
        @Size(max = 250) String observacao) {
}
