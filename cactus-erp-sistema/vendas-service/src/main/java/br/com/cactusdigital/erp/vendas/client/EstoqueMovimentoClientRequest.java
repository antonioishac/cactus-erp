package br.com.cactusdigital.erp.vendas.client;

import java.math.BigDecimal;

/**
 * Espelha o {@code EstoqueMovimentoRequest} do controle-estoque-service — cópia
 * deliberada, não uma dependência Java entre os dois serviços (limite de serviço: cada
 * um com seu próprio contrato). Mesmo padrão exato de
 * {@code gestao-compras-service.EstoqueMovimentoClientRequest}.
 */
public record EstoqueMovimentoClientRequest(
        Long produtoId,
        Long depositoId,
        Long loteId,
        Long numeroSerieId,
        String tipoMovimento,
        String direcaoAjuste,
        BigDecimal quantidade,
        BigDecimal custoUnitario,
        String origemTipo,
        Long origemId,
        Long aprovadorId,
        String observacao) {
}
