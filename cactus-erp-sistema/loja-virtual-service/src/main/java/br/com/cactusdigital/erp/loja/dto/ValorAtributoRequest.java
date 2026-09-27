package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ValorAtributoRequest(
        @NotNull Long atributoCategoriaId,
        @NotBlank @Size(max = 60) String valor) {
}
