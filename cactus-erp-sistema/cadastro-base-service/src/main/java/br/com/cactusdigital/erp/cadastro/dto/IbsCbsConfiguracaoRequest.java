package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Só a classificação tributária oficial ({@code cClassTrib}, 6 dígitos) — o resto vem do catálogo. */
public record IbsCbsConfiguracaoRequest(
        @NotBlank @Pattern(regexp = "\\d{6}", message = "cClassTrib tem 6 dígitos") String classificacaoTributaria) {
}
