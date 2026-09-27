package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoTelefone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TelefoneDTO(
        @NotNull TipoTelefone tipoTelefone,
        @NotBlank @Size(max = 15) String numero,
        boolean principal) {
}
