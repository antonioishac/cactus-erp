package br.com.cactusdigital.erp.fiscal.dto;

import java.math.BigDecimal;

public record ListaServicoMunicipalResponse(
        Long id,
        String codigo,
        String descricao,
        String codigoTributacaoMunicipio,
        BigDecimal aliquotaIssPadrao) {
}
