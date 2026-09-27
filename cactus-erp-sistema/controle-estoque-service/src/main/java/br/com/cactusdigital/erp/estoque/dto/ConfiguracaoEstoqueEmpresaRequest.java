package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.MetodoCusteio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ConfiguracaoEstoqueEmpresaRequest(
        @NotNull MetodoCusteio metodoCusteio,
        @NotNull @PositiveOrZero BigDecimal limiteAprovacaoAjuste) {
}
