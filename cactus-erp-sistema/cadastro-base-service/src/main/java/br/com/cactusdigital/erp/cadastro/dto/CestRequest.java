package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CestRequest(@NotBlank @Size(max = 7) String codigo, @NotBlank @Size(max = 1000) String descricao, Long ncmId) {
}
