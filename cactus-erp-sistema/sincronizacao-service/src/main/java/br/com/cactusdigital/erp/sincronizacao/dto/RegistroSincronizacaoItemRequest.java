package br.com.cactusdigital.erp.sincronizacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

/** Um item dentro do envelope de {@link LoteSincronizacaoRequest} — ver seção 5.2 do FASE6_SINCRONIZACAO_PDV.md. */
public record RegistroSincronizacaoItemRequest(
        @NotBlank String idOrigem,
        @NotNull JsonNode payload) {
}
