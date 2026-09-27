package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoNaturezaFinanceira;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NaturezaFinanceiraRequest(
        @NotNull Long empresaId,
        @NotBlank @Size(max = 10) String codigo,
        @NotBlank @Size(max = 100) String descricao,
        @NotNull TipoNaturezaFinanceira tipo) {
}
