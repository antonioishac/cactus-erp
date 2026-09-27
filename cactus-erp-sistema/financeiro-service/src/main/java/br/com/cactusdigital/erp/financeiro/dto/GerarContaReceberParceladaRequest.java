package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Espelho de {@code GerarContaPagarParceladaRequest} pro lado de cliente — ver Javadoc lá. */
public record GerarContaReceberParceladaRequest(
        @NotNull Long empresaId,
        @NotNull Long clienteId,
        @NotNull Long naturezaFinanceiraId,
        @NotNull Long tipoDocumentoId,
        Long contaBancariaId,
        @Size(max = 30) String numeroDocumento,
        @Size(max = 250) String descricao,
        @NotNull LocalDate dataEmissao,
        @NotNull @Min(1) Integer quantidadeParcelas,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valorTotal,
        @NotNull LocalDate primeiroVencimento,
        @NotNull @Min(0) Integer intervaloEntreParcelasDias,
        LocalDate descontoAte,
        @DecimalMin(value = "0.0001") BigDecimal percentualDesconto,
        @DecimalMin("0") BigDecimal percentualMultaAtraso,
        @DecimalMin("0") BigDecimal percentualJurosDiaAtraso) {
}
