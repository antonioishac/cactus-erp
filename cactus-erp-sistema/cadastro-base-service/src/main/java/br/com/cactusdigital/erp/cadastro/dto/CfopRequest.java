package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CfopRequest(@NotNull Integer codigo, @NotBlank @Size(max = 1000) String descricao, @Size(max = 1000) String aplicacao) {
}
