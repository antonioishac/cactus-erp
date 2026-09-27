package br.com.cactusdigital.erp.sincronizacao.dto;

import java.time.Instant;

public record RegistroSincronizacaoResponse(
        Long id,
        Long loteId,
        Long lojaId,
        String dispositivoId,
        String tipoRegistro,
        String idOrigem,
        String chaveIdempotencia,
        String status,
        int tentativas,
        String motivoErro,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
