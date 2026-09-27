package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.SituacaoPassoSaga;
import java.time.Instant;

public record SagaExecucaoPassoResponse(
        Long id,
        String nome,
        SituacaoPassoSaga situacao,
        String motivoErro,
        Instant dataEvento) {
}
