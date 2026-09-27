package br.com.cactusdigital.erp.estoque.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record InventarioFisicoContagemRequest(@NotNull @PositiveOrZero BigDecimal quantidadeContada) {
}
