package br.com.cactusdigital.erp.compras.entity;

/** Um {@link SagaExecucaoPasso} só existe depois do passo ter sido {@code EXECUTADO} — se o passo nunca chegou a rodar (a saga falhou antes dele), ele simplesmente não gera linha nenhuma. Mesmo desenho de {@code vendas-service.SituacaoPassoSaga}. */
public enum SituacaoPassoSaga {
    EXECUTADO,
    COMPENSADO,
    FALHOU_AO_COMPENSAR
}
