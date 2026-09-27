package br.com.cactusdigital.erp.bi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EstoqueValorIndicadorResponse(
        LocalDate dataReferencia,
        Long depositoId,
        String depositoNome,
        Long produtoId,
        String produtoNome,
        BigDecimal quantidade,
        BigDecimal custoMedioPonderado,
        BigDecimal valorTotal) {
}
