package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

public record VendaItemClienteDTO(
        Long id,
        Long produtoId,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        BigDecimal valorDesconto) {
}
