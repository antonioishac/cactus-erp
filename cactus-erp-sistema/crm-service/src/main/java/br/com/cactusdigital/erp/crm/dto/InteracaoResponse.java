package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.TipoInteracao;
import java.time.Instant;

public record InteracaoResponse(
        Long id,
        Long leadId,
        Long oportunidadeId,
        TipoInteracao tipo,
        Instant dataInteracao,
        Long usuarioId,
        String descricao) {
}
