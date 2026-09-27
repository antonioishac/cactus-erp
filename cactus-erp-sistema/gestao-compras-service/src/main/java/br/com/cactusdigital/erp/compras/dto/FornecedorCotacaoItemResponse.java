package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;

public record FornecedorCotacaoItemResponse(
        Long id,
        Long produtoId,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal) {
}
