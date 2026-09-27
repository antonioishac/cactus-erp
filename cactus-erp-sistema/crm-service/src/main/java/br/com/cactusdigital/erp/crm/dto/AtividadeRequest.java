package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.TipoAtividade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** Pelo menos um de {@code leadId}/{@code oportunidadeId} precisa ser preenchido — validado em {@code AtividadeService#criar}. */
public record AtividadeRequest(
        Long leadId,
        Long oportunidadeId,
        @NotNull TipoAtividade tipo,
        Long responsavelId,
        @NotNull Instant dataHora,
        @Size(max = 500) String descricao) {
}
