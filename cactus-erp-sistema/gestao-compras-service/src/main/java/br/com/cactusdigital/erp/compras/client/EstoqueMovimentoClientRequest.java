package br.com.cactusdigital.erp.compras.client;

import java.math.BigDecimal;

/**
 * Espelha o {@code EstoqueMovimentoRequest} do controle-estoque-service — cópia
 * deliberada, não uma dependência Java entre os dois serviços (limite de serviço: cada
 * um com seu próprio contrato). {@code tipoMovimento}/{@code origemTipo} são
 * {@link String} (não os enums do outro serviço) — o Jackson do lado de lá desserializa
 * a string contra o próprio enum dele normalmente.
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
