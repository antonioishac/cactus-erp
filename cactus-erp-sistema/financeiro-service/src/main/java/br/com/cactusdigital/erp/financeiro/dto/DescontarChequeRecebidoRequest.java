package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Desconto em banco — o dinheiro entra imediatamente (banco assume o risco), diferente
 * de {@code DepositarChequeRecebidoRequest} (que só confirma na compensação). */
public record DescontarChequeRecebidoRequest(@NotNull Long contaBancariaId, LocalDate dataDesconto) {
}
