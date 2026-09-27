package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Uma linha do relatório de vendas por período — só vendas {@code CONFIRMADA}. */
public record VendaPeriodoItemResponse(
        Long vendaId,
        LocalDate dataVenda,
        Long empresaId,
        Long vendedorId,
        Long clienteId,
        BigDecimal valorTotal) {
}
