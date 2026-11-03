package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Faixa a inutilizar. Limites do leiaute: série até 3 dígitos (0-999), número até 9 dígitos, justificativa de 15 a 255
 * caracteres. A série é a configurada para NF-e na empresa quando omitida.
 */
public record InutilizarNumeracaoNfeRequest(
        @NotNull Long empresaId,
        @Size(max = 3) String serie,
        @NotNull @Positive @Max(999_999_999) Long numeroInicial,
        @NotNull @Positive @Max(999_999_999) Long numeroFinal,
        @NotBlank @Size(min = 15, max = 255) String justificativa) {
}
