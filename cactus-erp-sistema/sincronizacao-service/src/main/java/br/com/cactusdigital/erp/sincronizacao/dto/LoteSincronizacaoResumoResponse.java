package br.com.cactusdigital.erp.sincronizacao.dto;

import java.time.Instant;

public record LoteSincronizacaoResumoResponse(
        Long id,
        Long lojaId,
        String dispositivoId,
        String tipoRegistro,
        int totalRegistros,
        int totalAceitos,
        int totalRejeitados,
        Instant dataCriacao) {
}
