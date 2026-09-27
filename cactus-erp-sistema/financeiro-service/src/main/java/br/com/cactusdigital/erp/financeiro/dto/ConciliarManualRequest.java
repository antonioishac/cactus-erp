package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotNull;

public record ConciliarManualRequest(@NotNull Long movimentoCaixaBancoId) {
}
