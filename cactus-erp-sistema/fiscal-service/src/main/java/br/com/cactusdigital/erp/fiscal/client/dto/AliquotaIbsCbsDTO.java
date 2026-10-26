package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

/** Espelha {@code AliquotaIbsCbsResponse} do cadastro-base-service: alíquotas (%) do ano de emissão. */
public record AliquotaIbsCbsDTO(
        int ano,
        BigDecimal aliquotaIbsUf,
        BigDecimal aliquotaIbsMunicipio,
        BigDecimal aliquotaCbs) {
}
