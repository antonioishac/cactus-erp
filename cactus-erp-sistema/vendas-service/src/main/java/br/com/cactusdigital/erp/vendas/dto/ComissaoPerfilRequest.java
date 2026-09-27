package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ComissaoPerfilRequest(
        @Size(max = 3) String codigo,
        @NotBlank @Size(max = 100) String nome,
        @NotNull Situacao situacao) {
}
