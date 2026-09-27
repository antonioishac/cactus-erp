package br.com.cactusdigital.erp.sincronizacao.dto;

import java.math.BigDecimal;

/** Mirror de {@code SaldoAgregadoResponse} (controle-estoque-service). */
public record SaldoAgregadoSincronizacaoResponse(
        Long produtoId,
        BigDecimal quantidadeTotal,
        BigDecimal quantidadeReservada,
        BigDecimal quantidadeDisponivel) {
}
