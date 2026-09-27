package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.TipoFrete;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Nasce em RASCUNHO, sem itens — adicione via {@code POST /orcamentos/{id}/itens}, mesmo padrão de {@code PedidoCompraRequest}. */
public record VendaOrcamentoRequest(
        @NotNull Long condicoesPagamentoId,
        @NotNull Long vendedorId,
        @NotNull Long clienteId,
        Long transportadoraId,
        @Size(max = 20) String codigo,
        LocalDate dataEntrega,
        LocalDate validade,
        TipoFrete tipoFrete,
        BigDecimal valorFrete,
        BigDecimal taxaComissao,
        BigDecimal taxaDesconto,
        @Size(max = 250) String observacao) {
}
