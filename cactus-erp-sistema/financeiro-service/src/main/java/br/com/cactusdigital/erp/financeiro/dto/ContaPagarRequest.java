package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * {@code MANUAL} avulso (1 parcela) — pra lançamento manual parcelado, ver
 * {@code GerarParceladoRequest}/{@code POST /contas-pagar/gerar-parcelado}.
 * {@code naturezaFinanceiraId}/{@code tipoDocumentoId} obrigatórios desde 2026-09-06
 * (fecha gap contra {@code FIN_NATUREZA_FINANCEIRA}/{@code FIN_DOCUMENTO_ORIGEM} do
 * fenix.sql — ver Javadoc de {@code ContaPagar}); {@code contaBancariaId}/
 * {@code descontoAte}/{@code percentualDesconto}/{@code percentualMultaAtraso}/
 * {@code percentualJurosDiaAtraso} são opcionais.
 */
public record ContaPagarRequest(
        @NotNull Long empresaId,
        @NotNull Long fornecedorId,
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
