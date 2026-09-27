package br.com.cactusdigital.erp.common.saga;

/**
 * Callback opcional pra persistir o estado de uma execução de saga (ver Javadoc de
 * {@link SagaOrchestrator}) — todo método é default no-op, então um chamador que só quer
 * o comportamento de compensação (sem trilha de auditoria persistida) pode ignorar essa
 * interface por completo.
 * <p>
 * Cada serviço que orquestra uma saga decide onde/como persistir (mesmo critério de
 * {@code BaseEntity} — o mecanismo genérico mora no {@code common}, a tabela concreta
 * mora no serviço dono da transação de negócio, ex: {@code vendas-service.SagaExecucao}).
 * Nenhum método aqui deve lançar exceção — {@link SagaOrchestrator} não trata falha de
 * auditoria como falha da saga, então implementações devem capturar seus próprios erros
 * de persistência internamente se precisarem.
 */
public interface SagaAuditor<C> {

    default void aoIniciar(String sagaNome, C contexto) {
    }

    default void aoExecutarPasso(String sagaNome, String passoNome, C contexto) {
    }

    default void aoCompensarPasso(String sagaNome, String passoNome, C contexto) {
    }

    default void aoFalharCompensacao(String sagaNome, String passoNome, C contexto, Exception motivo) {
    }

    /** Chamado uma vez, quando a saga falha — depois de todas as tentativas de compensação (bem-sucedidas ou não) já terem rodado. */
    default void aoFalhar(String sagaNome, String passoNomeQueFalhou, C contexto, RuntimeException causa, boolean compensacaoCompleta) {
    }

    default void aoConcluir(String sagaNome, C contexto) {
    }
}
