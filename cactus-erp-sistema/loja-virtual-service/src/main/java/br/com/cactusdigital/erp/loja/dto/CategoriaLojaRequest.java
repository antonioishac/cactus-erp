package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaLojaRequest(
        @NotNull Long lojaId,
        Long categoriaPaiId,
        @NotBlank @Size(max = 100) String nome,
        Integer ordem) {
}
