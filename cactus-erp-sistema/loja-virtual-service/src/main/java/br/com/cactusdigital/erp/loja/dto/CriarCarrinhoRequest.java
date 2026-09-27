package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.NotNull;

public record CriarCarrinhoRequest(
        @NotNull Long lojaId) {
}
