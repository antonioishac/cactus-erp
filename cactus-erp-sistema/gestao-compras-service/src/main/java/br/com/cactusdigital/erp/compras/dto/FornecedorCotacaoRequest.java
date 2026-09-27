package br.com.cactusdigital.erp.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FornecedorCotacaoRequest(
        @NotNull Long fornecedorId,
        @Size(max = 50) String prazoEntrega,
        @Size(max = 50) String condicoesPagamento) {
}
