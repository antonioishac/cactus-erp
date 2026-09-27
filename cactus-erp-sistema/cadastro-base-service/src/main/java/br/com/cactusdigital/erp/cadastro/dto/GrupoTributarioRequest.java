package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GrupoTributarioRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Pattern(regexp = "[0-9]", message = "Origem da mercadoria deve ser um dígito de 0 a 9 (campo orig da NFe)") String origemMercadoria,
        String observacao,
        @NotNull Situacao situacao) {
}
