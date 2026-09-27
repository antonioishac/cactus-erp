package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoContato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContatoDTO(
        @NotNull TipoContato tipoContato,
        @NotBlank @Size(max = 250) String valor,
        boolean principal) {
}
