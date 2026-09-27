package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AdicionarItemCarrinhoRequest(
        @NotNull Long varianteProdutoLojaId,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade) {
}
