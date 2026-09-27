package br.com.cactusdigital.erp.bi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurvaAbcIndicadorResponse(
        LocalDate dataReferencia,
        Long depositoId,
        String depositoNome,
        Long produtoId,
        String produtoNome,
        BigDecimal valorMovimentado,
        BigDecimal percentualParticipacao,
        BigDecimal percentualAcumulado,
        String classificacao) {
}
