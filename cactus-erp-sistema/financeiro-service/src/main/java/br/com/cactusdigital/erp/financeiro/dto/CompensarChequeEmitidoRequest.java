package br.com.cactusdigital.erp.financeiro.dto;

import java.time.LocalDate;

/** {@code dataCompensacao} nulo = usa {@code LocalDate.now()}. */
public record CompensarChequeEmitidoRequest(LocalDate dataCompensacao) {
}
