package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.SituacaoPassoSaga;
import java.time.Instant;

public record SagaExecucaoPassoResponse(
        Long id,
        String nome,
        SituacaoPassoSaga situacao,
        String motivoErro,
        Instant dataEvento) {
}
