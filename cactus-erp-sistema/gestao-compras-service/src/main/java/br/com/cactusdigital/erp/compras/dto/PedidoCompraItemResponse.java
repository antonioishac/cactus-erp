package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;

public record PedidoCompraItemResponse(
        Long id,
        Long produtoId,
        Long unidadeCompraId,
        BigDecimal fatorConversaoEstoque,
        BigDecimal quantidade,
        BigDecimal quantidadeRecebida,
        BigDecimal quantidadePendente,
        BigDecimal valorUnitario,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal) {
}
