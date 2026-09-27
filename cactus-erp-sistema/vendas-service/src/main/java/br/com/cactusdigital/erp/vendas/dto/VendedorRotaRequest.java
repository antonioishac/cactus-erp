package br.com.cactusdigital.erp.vendas.dto;

import jakarta.validation.constraints.NotNull;

public record VendedorRotaRequest(
        @NotNull Long clienteId,
        Integer posicao) {
}
