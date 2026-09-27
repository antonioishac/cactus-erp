package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.saga.SagaAuditor;
import br.com.cactusdigital.erp.vendas.entity.SagaExecucao;
import br.com.cactusdigital.erp.vendas.entity.SituacaoPassoSaga;
import br.com.cactusdigital.erp.vendas.entity.VendaCabecalho;

/**
 * Implementação concreta de {@code SagaAuditor} pra {@code VendaService#confirmar} —
 * delega toda persistência pra {@link SagaAuditoriaService} (transações
 * {@code REQUIRES_NEW}, ver Javadoc dela pro porquê disso ser indispensável, não só uma
 * escolha estilística).
 * <p>
 * <b>Não é um {@code @Component}/bean singleton, de propósito</b>: guarda o id do
 * {@link SagaExecucao} criado em {@link #aoIniciar} como estado mutável de instância
 * entre as chamadas de callback seguintes — uma instância nova é criada a cada chamada de
 * {@code VendaService#confirmar} (`new VendaSagaAuditor(...)`), nunca reaproveitada entre
 * execuções.
 */
class VendaSagaAuditor implements SagaAuditor<VendaCabecalho> {

    private final SagaAuditoriaService auditoriaService;
    private Long sagaExecucaoId;

    VendaSagaAuditor(SagaAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void aoIniciar(String sagaNome, VendaCabecalho venda) {
        sagaExecucaoId = auditoriaService.iniciar(sagaNome, "VENDA", venda.getId());
    }

    @Override
    public void aoExecutarPasso(String sagaNome, String passoNome, VendaCabecalho venda) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.EXECUTADO, null);
    }

    @Override
    public void aoCompensarPasso(String sagaNome, String passoNome, VendaCabecalho venda) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.COMPENSADO, null);
    }

    @Override
    public void aoFalharCompensacao(String sagaNome, String passoNome, VendaCabecalho venda, Exception motivo) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.FALHOU_AO_COMPENSAR, truncar(motivo.getMessage()));
    }

    @Override
    public void aoFalhar(String sagaNome, String passoNomeQueFalhou, VendaCabecalho venda, RuntimeException causa, boolean compensacaoCompleta) {
        auditoriaService.finalizarComFalha(sagaExecucaoId, passoNomeQueFalhou, truncar(causa.getMessage()), compensacaoCompleta);
    }

    @Override
    public void aoConcluir(String sagaNome, VendaCabecalho venda) {
        auditoriaService.finalizarComSucesso(sagaExecucaoId);
    }

    /** {@code motivo_erro} é {@code VARCHAR(500)} — mesma cautela já registrada em `ContaPagarService.numeroDocumento` (Fase financeiro): nunca confiar que uma mensagem de exceção de terceiro (ex: `RestClientException`) vai caber sem checar. */
    private static String truncar(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.length() > 500 ? texto.substring(0, 500) : texto;
    }
}
