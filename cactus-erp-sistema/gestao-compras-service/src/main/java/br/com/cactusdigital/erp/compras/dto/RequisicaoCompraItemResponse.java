package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;

public record RequisicaoCompraItemResponse(Long id, Long produtoId, BigDecimal quantidade) {
}
