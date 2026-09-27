package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Compartilhado entre {@code ContaPagarController}/{@code ContaReceberController}.
 * Atualiza só a categorização/config do título (nunca valor/vencimento/situação —
 * esses são imutáveis pós-criação, mudam só por ação dedicada). Fecha a lacuna de
 * títulos nascidos de integração ({@code VENDA}/{@code COMPRA}), que não têm como
 * informar {@code naturezaFinanceiraId}/{@code tipoDocumentoId} no momento da geração
 * automática — o time financeiro classifica depois.
 */
public record AtualizarClassificacaoTituloRequest(
        @NotNull Long naturezaFinanceiraId,
        @NotNull Long tipoDocumentoId,
        Long contaBancariaId,
        LocalDate descontoAte,
        @DecimalMin(value = "0.0001") BigDecimal percentualDesconto,
        @DecimalMin("0") BigDecimal percentualMultaAtraso,
        @DecimalMin("0") BigDecimal percentualJurosDiaAtraso) {
}
