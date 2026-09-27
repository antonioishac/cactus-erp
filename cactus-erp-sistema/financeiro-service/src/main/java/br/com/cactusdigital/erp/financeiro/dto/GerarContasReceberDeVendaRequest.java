package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/** Chamado pelo {@code vendas-service} ao confirmar uma venda — 1 {@code ContaReceber} por parcela. */
public record GerarContasReceberDeVendaRequest(
        @NotNull Long vendaId,
        @NotNull Long empresaId,
        @NotNull Long clienteId,
        @NotNull LocalDate dataEmissao,
        @NotEmpty @Valid List<ParcelaVendaRequest> parcelas) {
}
