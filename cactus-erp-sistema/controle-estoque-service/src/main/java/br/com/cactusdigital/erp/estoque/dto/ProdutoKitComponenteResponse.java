package br.com.cactusdigital.erp.estoque.dto;

import java.math.BigDecimal;

public record ProdutoKitComponenteResponse(
        Long id,
        Long produtoKitId,
        Long produtoComponenteId,
        BigDecimal quantidade) {
}
