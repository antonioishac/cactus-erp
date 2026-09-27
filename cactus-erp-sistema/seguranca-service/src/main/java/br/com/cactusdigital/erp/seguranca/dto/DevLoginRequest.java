package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.NotBlank;

public record DevLoginRequest(@NotBlank String login, @NotBlank String senha) {
}
