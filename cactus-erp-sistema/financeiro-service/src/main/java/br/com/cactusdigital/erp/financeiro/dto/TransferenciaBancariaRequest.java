package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenciaBancariaRequest(
        @NotNull Long contaOrigemId,
        @NotNull Long contaDestinoId,
        LocalDate dataTransferencia,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valor,
        @Size(max = 250) String historico,
        Long usuarioId) {
}
