package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma linha do relatório de vendas por período. {@code B2B}: só {@code CONFIRMADA}, {@code vendaId} =
 * {@code VendaCabecalho.id}. {@code PDV}: só {@code FECHADA}, {@code vendaId} = {@code VendaPdv.id},
 * {@code vendedorId} sempre nulo (balcão tem operador, não vendedor) e {@code valorTotal} = valor final
 * menos o valor já devolvido.
 */
public record VendaPeriodoItemResponse(
        Long vendaId,
        LocalDate dataVenda,
        Long empresaId,
        Long vendedorId,
        Long clienteId,
        BigDecimal valorTotal,
        CanalVenda canal) {
}
