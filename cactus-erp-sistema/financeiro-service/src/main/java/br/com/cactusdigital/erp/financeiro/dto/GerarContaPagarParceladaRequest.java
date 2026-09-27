package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parcelamento nativo pra lançamento manual — fecha o gap contra
 * {@code FIN_LANCAMENTO_PAGAR} do fenix.sql (que tinha {@code QUANTIDADE_PARCELA}/
 * {@code PRIMEIRO_VENCIMENTO}/{@code INTERVALO_ENTRE_PARCELAS} no cabeçalho; aqui não há
 * uma entidade "lançamento" separada — as N parcelas geradas compartilham
 * {@code loteId}, ver Javadoc de {@code ContaPagar}). Valor dividido igualmente entre as
 * parcelas, última parcela absorve o resto da divisão — mesmo critério de
 * {@code ContaReceberService#gerarContasReceber}.
 */
public record GerarContaPagarParceladaRequest(
        @NotNull Long empresaId,
        @NotNull Long fornecedorId,
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
