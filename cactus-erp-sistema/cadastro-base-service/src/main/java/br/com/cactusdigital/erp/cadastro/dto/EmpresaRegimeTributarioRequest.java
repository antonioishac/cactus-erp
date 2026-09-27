package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmpresaRegimeTributarioRequest(
        @NotNull @Min(2000) @Max(2100) Integer anoCalendario,
        @NotNull @Pattern(regexp = "[123]", message = "CRT deve ser 1, 2 ou 3") String crt) {
}
