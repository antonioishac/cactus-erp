package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.SituacaoNumeroSerie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NumeroSerieRequest(
        @NotNull Long produtoId,
        @NotBlank @Size(max = 100) String numeroSerie,
        Long depositoId,
        @NotNull SituacaoNumeroSerie situacao) {
}
