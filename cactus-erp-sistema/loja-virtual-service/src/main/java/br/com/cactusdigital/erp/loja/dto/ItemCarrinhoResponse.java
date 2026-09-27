package br.com.cactusdigital.erp.loja.dto;

import java.math.BigDecimal;

public record ItemCarrinhoResponse(
        Long id,
        Long varianteProdutoLojaId,
        String skuVariante,
        String nomeProduto,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorTotal) {
}
