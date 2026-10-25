package br.com.cactusdigital.erp.bi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturamentoDiarioIndicadorResponse(
        LocalDate dataReferencia,
        Long empresaId,
        String canal,
        Integer quantidadeVendas,
        BigDecimal valorBruto,
        BigDecimal valorDevolvido,
        BigDecimal valorLiquido,
        BigDecimal ticketMedio) {
}
