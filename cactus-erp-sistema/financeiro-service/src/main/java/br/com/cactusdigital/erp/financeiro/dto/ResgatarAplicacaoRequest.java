package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/** {@code valorResgatado} pode ser diferente de {@code valorAplicado} pra qualquer lado
 * (rendimento positivo ou negativo) — validado só como {@code > 0}, não contra o valor
 * aplicado. */
public record ResgatarAplicacaoRequest(
        LocalDate dataResgate,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valorResgatado) {
}
