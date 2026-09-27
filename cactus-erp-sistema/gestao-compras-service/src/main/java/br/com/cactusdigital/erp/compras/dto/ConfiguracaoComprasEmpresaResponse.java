package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;

public record ConfiguracaoComprasEmpresaResponse(Long id, Long empresaId, BigDecimal limiteAprovacaoPedido) {
}
