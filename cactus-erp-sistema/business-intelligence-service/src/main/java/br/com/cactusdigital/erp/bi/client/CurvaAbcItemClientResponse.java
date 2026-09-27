package br.com.cactusdigital.erp.bi.client;

import java.math.BigDecimal;

/** Espelha o {@code CurvaAbcItemResponse} do controle-estoque-service — cópia
 * deliberada, não uma dependência Java entre os dois serviços (limite de serviço). */
public record CurvaAbcItemClientResponse(
        Long produtoId,
        BigDecimal valorMovimentado,
        BigDecimal percentualParticipacao,
        BigDecimal percentualAcumulado,
        String classificacao) {
}
