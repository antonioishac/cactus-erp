package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CentroCustoRequest(
        @NotNull Long empresaId,
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 150) String nome,
        Situacao situacao) {
}
