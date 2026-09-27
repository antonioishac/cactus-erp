package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.SituacaoSagaExecucao;
import java.time.Instant;
import java.util.List;

/** Trilha de auditoria de uma execução de saga orquestrada (ver Javadoc de {@code SagaExecucao}/{@code SagaOrchestrator}). Consulta only — não existe criação/edição manual, a linha nasce inteiramente de dentro de {@code VendaService#confirmar}. */
public record SagaExecucaoResponse(
        Long id,
        String sagaNome,
        String referenciaTipo,
        Long referenciaId,
        SituacaoSagaExecucao situacao,
        String passoFalhouNome,
        String motivoErro,
        Instant dataInicio,
        Instant dataFim,
        List<SagaExecucaoPassoResponse> passos) {
}
