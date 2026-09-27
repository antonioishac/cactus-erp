package br.com.cactusdigital.erp.estoque.dto;

import java.math.BigDecimal;

/** {@code classificacao}: A (até 80% acumulado), B (até 95%), C (o resto) — critério clássico de curva ABC. */
public record CurvaAbcItemResponse(
        Long produtoId,
        BigDecimal valorMovimentado,
        BigDecimal percentualParticipacao,
        BigDecimal percentualAcumulado,
        String classificacao) {
}
