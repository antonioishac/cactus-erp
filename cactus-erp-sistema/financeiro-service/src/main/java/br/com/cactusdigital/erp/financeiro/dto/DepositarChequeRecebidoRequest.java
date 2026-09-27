package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DepositarChequeRecebidoRequest(@NotNull Long contaBancariaId, LocalDate dataDeposito) {
}
