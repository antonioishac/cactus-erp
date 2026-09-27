package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstornarBaixaRequest(
        @NotBlank @Size(max = 250) String motivo) {
}
