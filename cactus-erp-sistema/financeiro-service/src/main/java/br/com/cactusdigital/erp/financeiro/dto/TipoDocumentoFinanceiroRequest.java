package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoDocumentoFinanceiroRequest(
        @NotBlank @Size(max = 10) String codigo,
        @NotBlank @Size(max = 100) String descricao) {
}
