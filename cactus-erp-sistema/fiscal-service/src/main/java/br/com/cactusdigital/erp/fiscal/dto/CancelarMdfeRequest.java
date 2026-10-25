package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** {@code xJust} do evento de cancelamento: 15 a 255 caracteres (regra do leiaute de eventos da SEFAZ). */
public record CancelarMdfeRequest(
        @NotBlank @Size(min = 15, max = 255) String justificativa) {
}
