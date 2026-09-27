package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.TipoAtividade;
import java.time.Instant;

public record AtividadeResponse(
        Long id,
        Long leadId,
        Long oportunidadeId,
        TipoAtividade tipo,
        Long responsavelId,
        Instant dataHora,
        boolean concluida,
        Instant dataConclusao,
        String descricao) {
}
