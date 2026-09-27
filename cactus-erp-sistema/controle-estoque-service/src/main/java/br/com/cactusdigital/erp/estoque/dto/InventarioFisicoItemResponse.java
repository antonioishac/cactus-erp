package br.com.cactusdigital.erp.estoque.dto;

import java.math.BigDecimal;
import java.time.Instant;

/** {@code quantidadeSistema} — em contagem CEGA, o app cliente decide não exibir esse
 * campo ao contador; a API não esconde, só documenta a intenção (ver {@code TipoInventario}). */
public record InventarioFisicoItemResponse(
        Long id,
        Long produtoId,
        Long loteId,
        BigDecimal quantidadeSistema,
        BigDecimal quantidadeContada,
        BigDecimal quantidadeDivergencia,
        Long contadorId,
        Instant dataContagem) {
}
