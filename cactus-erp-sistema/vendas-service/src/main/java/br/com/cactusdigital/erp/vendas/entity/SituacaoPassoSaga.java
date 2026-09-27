package br.com.cactusdigital.erp.vendas.entity;

/** Um {@link SagaExecucaoPasso} só existe depois do passo ter sido {@code EXECUTADO} — se o passo nunca chegou a rodar (a saga falhou antes dele), ele simplesmente não gera linha nenhuma. */
public enum SituacaoPassoSaga {
    EXECUTADO,
    COMPENSADO,
    FALHOU_AO_COMPENSAR
}
