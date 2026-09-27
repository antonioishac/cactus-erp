package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NcmRequest(@NotBlank @Size(max = 8) String codigo, @NotBlank @Size(max = 1000) String descricao) {
}
