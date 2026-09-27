package br.com.cactusdigital.erp.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RecebimentoCompraItemRequest(
        @NotNull Long pedidoCompraItemId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidadeRecebida,
        @NotNull Long depositoId,
        Long loteId) {
}
