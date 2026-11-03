package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** {@code xJust} do evento 110111: 15 a 255 caracteres (leiaute de eventos da SEFAZ). */
public record CancelarNotaFiscalRequest(
        @NotBlank @Size(min = 15, max = 255) String justificativa) {
}
