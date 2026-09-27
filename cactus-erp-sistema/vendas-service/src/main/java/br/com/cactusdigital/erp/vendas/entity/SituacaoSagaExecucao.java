package br.com.cactusdigital.erp.vendas.entity;

/**
 * {@code EM_EXECUCAO} → {@code CONCLUIDA} (todos os passos executados com sucesso) OU
 * {@code COMPENSADA} (um passo falhou, mas todas as compensações dos passos anteriores
 * tiveram sucesso) OU {@code FALHA_COMPENSACAO} (um passo falhou E pelo menos uma
 * compensação também falhou — intervenção manual necessária, ver Javadoc de
 * {@code SagaOrchestrator}).
 */
public enum SituacaoSagaExecucao {
    EM_EXECUCAO,
    CONCLUIDA,
    COMPENSADA,
    FALHA_COMPENSACAO
}
