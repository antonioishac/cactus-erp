package br.com.cactusdigital.erp.ged.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PastaDocumentoRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 250) String descricao,
        Long pastaPaiId) {
}
