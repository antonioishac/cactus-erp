package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContadorRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 20) String crc,
        @Email @Size(max = 250) String email,
        @Size(max = 15) String telefone,
        @NotNull Situacao situacao) {
}
