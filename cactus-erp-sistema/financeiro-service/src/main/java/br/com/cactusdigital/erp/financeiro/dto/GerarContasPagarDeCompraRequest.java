package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/** Chamado pelo {@code gestao-compras-service} quando um {@code PedidoCompra} vira RECEBIDO_TOTAL — 1 {@code ContaPagar} por parcela. */
public record GerarContasPagarDeCompraRequest(
        @NotNull Long pedidoCompraId,
        @NotNull Long empresaId,
        @NotNull Long fornecedorId,
        @NotNull LocalDate dataEmissao,
        @NotEmpty @Valid List<ParcelaCompraRequest> parcelas) {
}
