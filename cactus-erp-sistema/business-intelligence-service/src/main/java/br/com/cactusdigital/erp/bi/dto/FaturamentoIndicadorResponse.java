package br.com.cactusdigital.erp.bi.dto;

import java.math.BigDecimal;
import java.util.List;

/** Totais do período + a série diária. {@code ticketMedio} do total é
 * {@code valorLiquido / quantidadeVendas} do período inteiro, nunca a média das médias diárias. */
public record FaturamentoIndicadorResponse(
        Integer quantidadeVendas,
        BigDecimal valorBruto,
        BigDecimal valorDevolvido,
        BigDecimal valorLiquido,
        BigDecimal ticketMedio,
        List<FaturamentoDiarioIndicadorResponse> dias) {
}
