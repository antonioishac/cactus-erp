package br.com.cactusdigital.erp.sincronizacao.dto;

import br.com.cactusdigital.erp.sincronizacao.entity.TipoRegistroSincronizacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Envelope de {@code POST /api/v1/sync/lotes} — ver seção 5.2 do FASE6_SINCRONIZACAO_PDV.md. */
public record LoteSincronizacaoRequest(
        @NotNull Long lojaId,
        @NotBlank String dispositivoId,
        @NotNull TipoRegistroSincronizacao tipoRegistro,
        @NotEmpty List<@Valid RegistroSincronizacaoItemRequest> registros) {
}
