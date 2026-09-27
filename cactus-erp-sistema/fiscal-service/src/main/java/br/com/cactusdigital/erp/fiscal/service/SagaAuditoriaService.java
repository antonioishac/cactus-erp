package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.entity.SagaExecucao;
import br.com.cactusdigital.erp.fiscal.entity.SagaExecucaoPasso;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoPassoSaga;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoSagaExecucao;
import br.com.cactusdigital.erp.fiscal.repository.SagaExecucaoPassoRepository;
import br.com.cactusdigital.erp.fiscal.repository.SagaExecucaoRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistência da auditoria de saga numa transação PRÓPRIA ({@code REQUIRES_NEW}) — mesmo
 * mecanismo e mesmo motivo exatos de {@code vendas-service}/
 * {@code gestao-compras-service.SagaAuditoriaService} (ver Javadoc lá pro relato completo
 * do bug real que motivou isto na 1ª aplicação): sem isto, os registros de auditoria de
 * uma saga que FALHOU seriam desfeitos pelo rollback da transação de
 * {@code NotaFiscalEletronicaService#gerarDeVenda}, justamente no caso em que a auditoria
 * mais importa.
 */
@Service
@RequiredArgsConstructor
class SagaAuditoriaService {

    private final SagaExecucaoRepository sagaExecucaoRepository;
    private final SagaExecucaoPassoRepository sagaExecucaoPassoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Long iniciar(String sagaNome, String referenciaTipo, Long referenciaId) {
        SagaExecucao sagaExecucao = sagaExecucaoRepository.save(SagaExecucao.builder()
                .sagaNome(sagaNome)
                .referenciaTipo(referenciaTipo)
                .referenciaId(referenciaId)
                .dataInicio(Instant.now())
                .build());
        return sagaExecucao.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void registrarPasso(Long sagaExecucaoId, String nome, SituacaoPassoSaga situacao, String motivoErro) {
        SagaExecucaoPasso passo = SagaExecucaoPasso.builder()
                .sagaExecucao(sagaExecucaoRepository.getReferenceById(sagaExecucaoId))
                .nome(nome)
                .situacao(situacao)
                .motivoErro(motivoErro)
                .dataEvento(Instant.now())
                .build();
        sagaExecucaoPassoRepository.save(passo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void finalizarComSucesso(Long sagaExecucaoId) {
        SagaExecucao sagaExecucao = sagaExecucaoRepository.getReferenceById(sagaExecucaoId);
        sagaExecucao.setSituacao(SituacaoSagaExecucao.CONCLUIDA);
        sagaExecucao.setDataFim(Instant.now());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void finalizarComFalha(Long sagaExecucaoId, String passoFalhouNome, String motivoErro, boolean compensacaoCompleta) {
        SagaExecucao sagaExecucao = sagaExecucaoRepository.getReferenceById(sagaExecucaoId);
        sagaExecucao.setSituacao(compensacaoCompleta ? SituacaoSagaExecucao.COMPENSADA : SituacaoSagaExecucao.FALHA_COMPENSACAO);
        sagaExecucao.setPassoFalhouNome(passoFalhouNome);
        sagaExecucao.setMotivoErro(motivoErro);
        sagaExecucao.setDataFim(Instant.now());
    }
}
