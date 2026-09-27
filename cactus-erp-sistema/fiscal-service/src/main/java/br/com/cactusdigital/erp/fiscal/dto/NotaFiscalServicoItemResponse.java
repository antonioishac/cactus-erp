package br.com.cactusdigital.erp.fiscal.dto;

import java.math.BigDecimal;

public record NotaFiscalServicoItemResponse(
        Long id,
        Long listaServicoId,
        String listaServicoDescricao,
        String discriminacao,
        String codigoTributacaoMunicipio,
        BigDecimal valorServicos,
        BigDecimal aliquotaIss,
        BigDecimal valorIss) {
}
