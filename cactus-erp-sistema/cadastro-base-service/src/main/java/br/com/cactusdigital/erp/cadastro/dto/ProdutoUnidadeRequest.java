package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoUnidadeRequest(
        @NotBlank @Size(max = 10) String sigla,
        @NotBlank @Size(max = 100) String descricao,
        boolean podeFracionar,
        @NotNull Situacao situacao) {
}
