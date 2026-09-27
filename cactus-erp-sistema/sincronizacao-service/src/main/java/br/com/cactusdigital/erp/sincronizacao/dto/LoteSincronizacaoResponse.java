package br.com.cactusdigital.erp.sincronizacao.dto;

import java.util.List;

/**
 * Resposta síncrona e imediata de {@code POST /api/v1/sync/lotes} — o PDV não espera o
 * processamento downstream terminar (ver seção 5.2 do FASE6_SINCRONIZACAO_PDV.md).
 * {@code loteId} é o {@code Long} do {@code LoteSincronizacao} (não um UUID como no
 * exemplo ilustrativo do documento — mesma convenção de PK numérica usada em todo o
 * resto do monorepo, sem necessidade real de UUID aqui).
 */
public record LoteSincronizacaoResponse(Long loteId, List<ResultadoRegistroResponse> resultados) {
}
