package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;

/** {@code percentualMultaAtraso}/{@code percentualJurosDiaAtraso} nulos = sem cobrança de atraso. */
public record ConfiguracaoFinanceiroEmpresaRequest(
        BigDecimal percentualMultaAtraso,
        BigDecimal percentualJurosDiaAtraso) {
}
