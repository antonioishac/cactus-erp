package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.SituacaoSagaExecucao;
import java.time.Instant;
import java.util.List;

/** Trilha de auditoria de uma execução de saga orquestrada (ver Javadoc de {@code SagaExecucao}/{@code common.saga.SagaOrchestrator}). Consulta only — a linha nasce inteiramente de dentro de {@code NotaFiscalEletronicaService#gerarDeVenda}. */
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
