package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;

/** Agregado de {@code VendaItem} por produto, dentro do período — ordenado por quantidade vendida decrescente. */
public record ProdutoMaisVendidoResponse(
        Long produtoId,
        BigDecimal quantidadeTotal,
        BigDecimal valorTotal) {
}
