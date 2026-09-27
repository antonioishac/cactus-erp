package br.com.cactusdigital.erp.vendas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotaFiscalModeloRequest(
        @NotBlank @Size(max = 2) String codigo,
        @Size(max = 100) String descricao,
        @Size(max = 10) String modelo) {
}
