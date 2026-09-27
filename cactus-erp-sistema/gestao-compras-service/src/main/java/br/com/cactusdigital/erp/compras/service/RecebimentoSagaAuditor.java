package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.common.saga.SagaAuditor;
import br.com.cactusdigital.erp.compras.entity.RecebimentoCompra;
import br.com.cactusdigital.erp.compras.entity.SagaExecucao;
import br.com.cactusdigital.erp.compras.entity.SituacaoPassoSaga;

/**
 * Implementação concreta de {@code SagaAuditor} pra
 * {@code RecebimentoCompraService#registrar} — mesmo desenho exato de
 * {@code vendas-service.VendaSagaAuditor}: delega toda persistência pra
 * {@link SagaAuditoriaService} ({@code REQUIRES_NEW}, indispensável — ver Javadoc dela).
 * <p>
 * <b>Não é um {@code @Component}/bean singleton, de propósito</b>: guarda o id do
 * {@link SagaExecucao} criado em {@link #aoIniciar} como estado mutável de instância —
 * uma instância nova é criada a cada chamada de {@code RecebimentoCompraService#registrar}.
 */
class RecebimentoSagaAuditor implements SagaAuditor<RecebimentoCompra> {

    private final SagaAuditoriaService auditoriaService;
    private Long sagaExecucaoId;

    RecebimentoSagaAuditor(SagaAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void aoIniciar(String sagaNome, RecebimentoCompra recebimento) {
        sagaExecucaoId = auditoriaService.iniciar(sagaNome, "RECEBIMENTO_COMPRA", recebimento.getId());
    }

    @Override
    public void aoExecutarPasso(String sagaNome, String passoNome, RecebimentoCompra recebimento) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.EXECUTADO, null);
    }

    @Override
    public void aoCompensarPasso(String sagaNome, String passoNome, RecebimentoCompra recebimento) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.COMPENSADO, null);
    }

    @Override
    public void aoFalharCompensacao(String sagaNome, String passoNome, RecebimentoCompra recebimento, Exception motivo) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.FALHOU_AO_COMPENSAR, truncar(motivo.getMessage()));
    }

    @Override
    public void aoFalhar(String sagaNome, String passoNomeQueFalhou, RecebimentoCompra recebimento, RuntimeException causa, boolean compensacaoCompleta) {
        auditoriaService.finalizarComFalha(sagaExecucaoId, passoNomeQueFalhou, truncar(causa.getMessage()), compensacaoCompleta);
    }

    @Override
    public void aoConcluir(String sagaNome, RecebimentoCompra recebimento) {
        auditoriaService.finalizarComSucesso(sagaExecucaoId);
    }

    /** {@code motivo_erro} é {@code VARCHAR(500)} — mesma cautela já registrada em `vendas-service.VendaSagaAuditor`. */
    private static String truncar(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.length() > 500 ? texto.substring(0, 500) : texto;
    }
}
