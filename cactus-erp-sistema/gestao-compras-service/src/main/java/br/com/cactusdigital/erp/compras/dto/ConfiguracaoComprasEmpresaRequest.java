package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;

/** {@code limiteAprovacaoPedido} nulo = sem exigência de aprovação. */
public record ConfiguracaoComprasEmpresaRequest(BigDecimal limiteAprovacaoPedido) {
}
