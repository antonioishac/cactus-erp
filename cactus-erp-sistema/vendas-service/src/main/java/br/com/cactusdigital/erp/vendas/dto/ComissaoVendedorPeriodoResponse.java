package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;

/** Agregado de {@code VendaComissao} por vendedor, dentro do período (por {@code dataLancamento}). */
public record ComissaoVendedorPeriodoResponse(
        Long vendedorId,
        BigDecimal valorComissaoTotal,
        long quantidadeVendas) {
}
