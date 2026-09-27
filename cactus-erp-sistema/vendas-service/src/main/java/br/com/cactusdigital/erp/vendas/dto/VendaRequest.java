package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.TipoFrete;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Nasce em RASCUNHO, sem itens — adicione via {@code POST /vendas/{id}/itens}, mesmo padrão de {@code PedidoCompraRequest}/{@code VendaOrcamentoRequest}. */
public record VendaRequest(
        @NotNull Long empresaId,
        @NotNull Long condicoesPagamentoId,
        @NotNull Long notaFiscalTipoId,
        @NotNull Long vendedorId,
        @NotNull Long clienteId,
        Long transportadoraId,
        @NotNull Long formaPagamentoId,
        LocalDate dataVenda,
        LocalDate dataSaida,
        String horaSaida,
        Integer numeroFatura,
        String localEntrega,
        String localCobranca,
        TipoFrete tipoFrete,
        BigDecimal taxaComissao,
        BigDecimal taxaDesconto,
        BigDecimal valorFrete,
        BigDecimal valorSeguro,
        Integer diaFixoParcela,
        String observacao) {
}
