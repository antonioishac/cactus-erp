package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.Size;

/** {@code historico} nulo = usa o histórico da própria linha do extrato. */
public record GerarAjusteConciliacaoRequest(@Size(max = 250) String historico) {
}
