package br.com.cactusdigital.erp.contabilidade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstornarLancamentoRequest(@NotBlank @Size(max = 250) String motivo) {
}
