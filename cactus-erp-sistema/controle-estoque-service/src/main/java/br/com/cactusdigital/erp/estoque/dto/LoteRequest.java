package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record LoteRequest(
        @NotNull Long produtoId,
        @NotBlank @Size(max = 60) String codigo,
        LocalDate dataFabricacao,
        LocalDate dataValidade,
        @NotNull Situacao situacao) {
}
