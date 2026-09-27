package br.com.cactusdigital.erp.estoque.dto;

import java.math.BigDecimal;

/**
 * Saldo somado de um produto em todos os depósitos de uma empresa — é este o formato
 * que o {@code sincronizacao-service} consome pra compor o pull de produto pro PDV
 * (Fase 6): o PDV não enxerga depósito, só um total por produto/loja. Nunca persistido,
 * calculado sob demanda a partir de {@code SaldoEstoque} (fonte da verdade continua
 * sendo o ledger, ver {@code EstoqueMovimentoService}).
 */
public record SaldoAgregadoResponse(
        Long produtoId,
        BigDecimal quantidadeTotal,
        BigDecimal quantidadeReservada,
        BigDecimal quantidadeDisponivel) {
}
