package br.com.cactusdigital.erp.cadastro.dto;

import java.math.BigDecimal;

/** Alíquotas (%) vigentes no ano de emissão — fonte única para quem emite documento fiscal. */
public record AliquotaIbsCbsResponse(
        int ano,
        BigDecimal aliquotaIbsUf,
        BigDecimal aliquotaIbsMunicipio,
        BigDecimal aliquotaCbs,
        String fundamento) {
}
