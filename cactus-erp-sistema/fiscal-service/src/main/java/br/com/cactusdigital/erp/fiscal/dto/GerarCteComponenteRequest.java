package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record GerarCteComponenteRequest(
        @NotBlank @Size(max = 15) String nome,
        @NotNull BigDecimal valor) {
}
