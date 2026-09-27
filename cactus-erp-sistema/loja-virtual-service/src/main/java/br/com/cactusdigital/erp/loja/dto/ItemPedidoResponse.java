package br.com.cactusdigital.erp.loja.dto;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long id,
        Long varianteProdutoLojaId,
        String skuSnapshot,
        String nomeSnapshot,
        String ncmSnapshot,
        String csosnSnapshot,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorTotal) {
}
