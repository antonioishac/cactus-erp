package br.com.cactusdigital.erp.fiscal.entity;

/** Um {@link SagaExecucaoPasso} só existe depois do passo ter sido {@code EXECUTADO}. Mesmo desenho de {@code vendas-service}/{@code gestao-compras-service.SituacaoPassoSaga}. */
public enum SituacaoPassoSaga {
    EXECUTADO,
    COMPENSADO,
    FALHOU_AO_COMPENSAR
}
