package br.com.cactusdigital.erp.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** {@code fatorConversaoEstoque} nulo assume 1 (compra e estoque na mesma unidade). */
public record PedidoCompraItemRequest(
        @NotNull Long produtoId,
        Long unidadeCompraId,
        @DecimalMin(value = "0.000001") BigDecimal fatorConversaoEstoque,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade,
        @NotNull @DecimalMin(value = "0") BigDecimal valorUnitario,
        BigDecimal taxaDesconto) {
}
