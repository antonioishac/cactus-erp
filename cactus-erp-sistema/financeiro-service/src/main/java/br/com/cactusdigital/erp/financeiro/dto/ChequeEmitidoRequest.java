package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ChequeEmitidoRequest(
        @NotNull Long contaBancariaId,
        @NotBlank @Size(max = 20) String numero,
        @NotNull LocalDate dataEmissao,
        LocalDate bomPara,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valor,
        @Size(max = 150) String nominalA) {
}
