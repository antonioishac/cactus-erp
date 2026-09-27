package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.vendas.entity.SagaExecucao;
import br.com.cactusdigital.erp.vendas.entity.SagaExecucaoPasso;
import br.com.cactusdigital.erp.vendas.entity.SituacaoPassoSaga;
import br.com.cactusdigital.erp.vendas.entity.SituacaoSagaExecucao;
import br.com.cactusdigital.erp.vendas.repository.SagaExecucaoPassoRepository;
import br.com.cactusdigital.erp.vendas.repository.SagaExecucaoRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistência da auditoria de saga (ver Javadoc de {@link SagaExecucao}) numa transação
 * PRÓPRIA ({@code REQUIRES_NEW}) — <b>achado real corrigido antes de considerar a fase
 * pronta</b>: sem isso, a trilha de auditoria some exatamente no caso em que ela mais
 * importa. {@code VendaService#confirmar} é {@code @Transactional}; quando a saga falha,
 * {@code SagaOrchestrator#executar} relança a exceção original (de propósito, ver seu
 * Javadoc) — e uma {@code RuntimeException} saindo de um método {@code @Transactional}
 * aciona o rollback padrão do Spring, que desfaria TAMBÉM os `INSERT`s de
 * {@code SagaExecucao}/{@code SagaExecucaoPasso} feitos até ali, já que originalmente
 * viviam na MESMA transação local da venda. Cada método aqui abre a própria transação
 * (mesmo espírito de {@code EdiBancarioService#tentarBaixaAutomaticaEmTransacaoPropria},
 * só que via anotação em vez de {@code TransactionTemplate} — não há lock pessimista
 * concorrente em jogo aqui pra justificar a versão programática) — comita
 * imediatamente, sobrevivendo ao rollback da transação de {@code confirmar} que vem
 * depois. Confirmado ao vivo: sem esta correção, {@code GET /api/v1/sagas} não mostrava
 * NENHUMA linha pra uma venda cuja confirmação falhou — a auditoria só "funcionava" no
 * caminho feliz, que é justamente o caso que menos precisa dela.
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
