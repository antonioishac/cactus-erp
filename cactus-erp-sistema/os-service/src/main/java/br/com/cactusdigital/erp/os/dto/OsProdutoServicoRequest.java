package br.com.cactusdigital.erp.os.dto;

import br.com.cactusdigital.erp.os.entity.TipoItemOs;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** {@code produtoId} obrigatório quando {@code tipo=PRODUTO} — validado em {@code OsAberturaService}. */
public record OsProdutoServicoRequest(
        @NotNull TipoItemOs tipo,
        Long produtoId,
        @Size(max = 250) String complemento,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade,
        @NotNull @DecimalMin(value = "0") BigDecimal valorUnitario,
        BigDecimal taxaDesconto) {
}
