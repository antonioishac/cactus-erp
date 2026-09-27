package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.saga.SagaAuditor;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.SagaExecucao;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoPassoSaga;

/**
 * Implementação concreta de {@code SagaAuditor} pra
 * {@code NotaFiscalEletronicaService#gerarDeVenda} — mesmo desenho exato de
 * {@code vendas-service.VendaSagaAuditor}/{@code gestao-compras-service.RecebimentoSagaAuditor}:
 * delega toda persistência pra {@link SagaAuditoriaService} ({@code REQUIRES_NEW},
 * indispensável — ver Javadoc dela).
 * <p>
 * <b>Não é um {@code @Component}/bean singleton, de propósito</b>: guarda o id do
 * {@link SagaExecucao} criado em {@link #aoIniciar} como estado mutável de instância —
 * uma instância nova é criada a cada chamada de
 * {@code NotaFiscalEletronicaService#gerarDeVenda}.
 */
class NotaFiscalSagaAuditor implements SagaAuditor<NotaFiscalEletronica> {

    private final SagaAuditoriaService auditoriaService;
    private Long sagaExecucaoId;

    NotaFiscalSagaAuditor(SagaAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void aoIniciar(String sagaNome, NotaFiscalEletronica nota) {
        sagaExecucaoId = auditoriaService.iniciar(sagaNome, "VENDA", nota.getVendaId());
    }

    @Override
    public void aoExecutarPasso(String sagaNome, String passoNome, NotaFiscalEletronica nota) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.EXECUTADO, null);
    }

    @Override
    public void aoCompensarPasso(String sagaNome, String passoNome, NotaFiscalEletronica nota) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.COMPENSADO, null);
    }

    @Override
    public void aoFalharCompensacao(String sagaNome, String passoNome, NotaFiscalEletronica nota, Exception motivo) {
        auditoriaService.registrarPasso(sagaExecucaoId, passoNome, SituacaoPassoSaga.FALHOU_AO_COMPENSAR, truncar(motivo.getMessage()));
    }

    @Override
    public void aoFalhar(String sagaNome, String passoNomeQueFalhou, NotaFiscalEletronica nota, RuntimeException causa, boolean compensacaoCompleta) {
        auditoriaService.finalizarComFalha(sagaExecucaoId, passoNomeQueFalhou, truncar(causa.getMessage()), compensacaoCompleta);
    }

    @Override
    public void aoConcluir(String sagaNome, NotaFiscalEletronica nota) {
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
