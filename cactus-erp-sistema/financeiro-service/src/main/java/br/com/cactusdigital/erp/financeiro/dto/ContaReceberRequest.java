package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Criação manual avulsa (1 parcela) — {@code origemTipo} nasce {@code MANUAL} (a origem
 * {@code VENDA} só nasce via {@code POST /contas-receber/gerar-de-venda}, chamado pelo
 * vendas-service). Pra lançamento manual parcelado, ver {@code GerarParceladoRequest}.
 * {@code naturezaFinanceiraId}/{@code tipoDocumentoId} obrigatórios desde 2026-09-06 —
 * ver Javadoc de {@code ContaReceber}.
 */
public record ContaReceberRequest(
        @NotNull Long empresaId,
        @NotNull Long clienteId,
        @NotNull Long naturezaFinanceiraId,
        @NotNull Long tipoDocumentoId,
        Long contaBancariaId,
        @Size(max = 30) String numeroDocumento,
        @Size(max = 250) String descricao,
        @NotNull LocalDate dataEmissao,
        @NotNull LocalDate dataVencimento,
        LocalDate descontoAte,
        @DecimalMin(value = "0.0001") BigDecimal percentualDesconto,
        @DecimalMin("0") BigDecimal percentualMultaAtraso,
        @DecimalMin("0") BigDecimal percentualJurosDiaAtraso,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valorOriginal) {
}
