package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaCompraRequest(
        @NotNull Integer numeroParcela,
        @NotNull LocalDate dataVencimento,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valor) {
}
