package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;

public record RecebimentoCompraItemResponse(
        Long id,
        Long pedidoCompraItemId,
        Long produtoId,
        BigDecimal quantidadeRecebida,
        Long depositoId,
        Long loteId) {
}
