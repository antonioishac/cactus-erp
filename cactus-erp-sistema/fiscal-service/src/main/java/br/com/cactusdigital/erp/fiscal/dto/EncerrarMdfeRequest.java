package br.com.cactusdigital.erp.fiscal.dto;

import java.time.LocalDate;

/**
 * Ambos opcionais: {@code null} = hoje / município de descarga do último CT-e do manifesto
 * (o fim natural da viagem). Informar só quando a viagem terminou num dia/município diferente.
 */
public record EncerrarMdfeRequest(
        LocalDate dataEncerramento,
        Integer municipioCodigoIbge) {
}
