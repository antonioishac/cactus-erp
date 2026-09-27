package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SetorRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 250) String descricao,
        @NotNull Situacao situacao) {
}
