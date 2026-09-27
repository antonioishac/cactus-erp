package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;

public record ConfiguracaoFinanceiroEmpresaResponse(
        Long id,
        Long empresaId,
        BigDecimal percentualMultaAtraso,
        BigDecimal percentualJurosDiaAtraso) {
}
