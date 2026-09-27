package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Compartilhado entre cheque emitido e recebido. {@code dataDevolucao} nulo = hoje. */
public record DevolverChequeRequest(LocalDate dataDevolucao, @NotBlank @Size(max = 250) String motivo) {
}
