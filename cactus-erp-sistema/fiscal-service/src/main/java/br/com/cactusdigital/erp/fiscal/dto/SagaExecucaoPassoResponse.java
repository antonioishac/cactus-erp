package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.SituacaoPassoSaga;
import java.time.Instant;

public record SagaExecucaoPassoResponse(
        Long id,
        String nome,
        SituacaoPassoSaga situacao,
        String motivoErro,
        Instant dataEvento) {
}
