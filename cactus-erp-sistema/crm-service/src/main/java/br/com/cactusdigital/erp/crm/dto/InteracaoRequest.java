package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.TipoInteracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** Pelo menos um de {@code leadId}/{@code oportunidadeId} precisa ser preenchido — validado em {@code InteracaoService#criar}. */
public record InteracaoRequest(
        Long leadId,
        Long oportunidadeId,
        @NotNull TipoInteracao tipo,
        Instant dataInteracao,
        Long usuarioId,
        @NotBlank @Size(max = 1000) String descricao) {
}
