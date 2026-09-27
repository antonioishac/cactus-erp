package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Nasce sem variantes — adicione via POST /produtos-loja/{id}/variantes. */
public record ProdutoLojaRequest(
        @NotNull Long lojaId,
        @NotNull Long categoriaLojaId,
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 2000) String descricao,
        @Size(max = 60) String marca) {
}
