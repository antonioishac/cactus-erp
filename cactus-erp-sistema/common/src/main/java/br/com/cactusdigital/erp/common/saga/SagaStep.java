package br.com.cactusdigital.erp.common.saga;

/**
 * Um passo de uma saga orquestrada (ver Javadoc de {@link SagaOrchestrator}). {@code C}
 * é o contexto compartilhado entre todos os passos da mesma execução — tipicamente a
 * entidade de negócio que está sendo processada (ex: a venda sendo confirmada).
 * <p>
 * {@link #compensar} é sempre <b>best-effort</b>: o {@link SagaOrchestrator} chama em
 * ordem reversa só os passos já {@link #executar}ados com sucesso, e uma falha na
 * compensação de um passo não impede a tentativa de compensar os passos anteriores —
 * ver Javadoc de {@link SagaOrchestrator} pro que acontece quando a própria compensação
 * falha (não é undo transacional de banco, é uma chamada de negócio à parte).
 */
public interface SagaStep<C> {

    /** Identifica o passo nos logs/auditoria — precisa ser único dentro da mesma saga. */
    String nome();

    /** A ação de negócio propriamente dita. Lança uma {@link RuntimeException} em caso de falha — é isso que aciona a compensação dos passos anteriores. */
    void executar(C contexto);

    /**
     * Desfaz o efeito de {@link #executar} — chamado só se ESTE passo já tinha sido
     * executado com sucesso quando um passo POSTERIOR falhou. Default no-op: nem todo
     * passo precisa de compensação (ex: um passo de leitura, ou uma escrita cujo rollback
     * já é coberto pela transação local — só sobrescreva quando o passo tiver efeito em
     * OUTRO serviço/sistema, que a transação local não desfaz).
     */
    default void compensar(C contexto) {
        // no-op — a maioria dos passos locais já é coberta pelo rollback do @Transactional.
    }
}
