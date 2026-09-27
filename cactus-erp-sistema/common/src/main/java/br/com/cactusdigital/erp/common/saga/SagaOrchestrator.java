package br.com.cactusdigital.erp.common.saga;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Motor genérico de <b>saga orquestrada</b> (orchestration-based, não choreography) —
 * um componente central (este mesmo, injetado no serviço que é "dono" da transação de
 * negócio) executa uma sequência de {@link SagaStep} contra um contexto compartilhado; se
 * um passo falha, compensa em ordem REVERSA todos os passos já executados com sucesso, e
 * relança a exceção original (não uma exceção nova) — quem chama {@link #executar} recebe
 * exatamente o mesmo tipo de erro que receberia sem a saga por trás, só que agora com a
 * garantia de que os efeitos parciais em outros serviços já foram (tentativamente)
 * desfeitos.
 * <p>
 * <b>Por que orquestração, e não coreografia (eventos)</b>: os fluxos-piloto deste
 * monorepo (confirmar venda, receber pedido de compra) já são chamadas SÍNCRONAS —
 * o serviço iniciador (ex: {@code vendas-service}) já sabe exatamente quem chamar e em
 * que ordem, não tem um workflow longo/assíncrono que justifique um coordenador
 * separado ou um barramento de eventos. Orquestração embutida no próprio serviço que já
 * inicia a transação é a solução mais simples que resolve o problema real — não nasce
 * um {@code saga-orchestrator-service} novo no monorepo por isto.
 * <p>
 * <b>Isto NÃO é uma transação distribuída de verdade</b> (não há 2PC nem XA) — é o padrão
 * Saga de mercado: cada passo comita a própria mudança de imediato (no seu próprio
 * serviço/transação local), e a "reversão" é uma NOVA operação de negócio compensatória,
 * não um rollback de banco. Duas consequências que todo {@link SagaStep} precisa
 * respeitar: (1) entre a falha de um passo e a compensação dos anteriores, o sistema
 * passa por um estado transitório onde os efeitos dos passos já executados são
 * visíveis a qualquer leitor concorrente (não há isolamento entre serviços); (2)
 * compensação é <b>best-effort</b> — se ela própria falhar (serviço remoto fora do ar,
 * por exemplo), não há novo retry automático aqui, só o registro em log/auditoria
 * (ver {@link SagaAuditor#aoFalharCompensacao}) pra intervenção manual. Isso é uma
 * limitação aceita, do mesmo espírito já registrado em {@code EstoqueClient}/
 * {@code FinanceiroClient} antes desta saga existir — a saga reduz a janela de
 * inconsistência, não a elimina por completo (só uma saga com fila/retry duráveis, tipo
 * outbox, eliminaria de verdade — fora de escopo desta primeira versão).
 * <p>
 * <b>Passos não precisam ser idempotentes pra a saga funcionar</b> — mas se forem
 * (ex: uma chamada remota que aceita uma chave de idempotência), a saga fica mais
 * robusta ainda contra retry duplo. Nenhum {@link SagaStep} piloto deste monorepo é
 * idempotente hoje (chamar {@code executar} duas vezes gera dois efeitos reais) — a
 * saga em si não resolve isso, só a compensação em caso de falha no MEIO da sequência.
 */
@Component
public class SagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestrator.class);

    public <C> void executar(String sagaNome, C contexto, List<SagaStep<C>> passos) {
        executar(sagaNome, contexto, passos, new SagaAuditor<C>() {
        });
    }

    public <C> void executar(String sagaNome, C contexto, List<SagaStep<C>> passos, SagaAuditor<C> auditor) {
        auditor.aoIniciar(sagaNome, contexto);
        List<SagaStep<C>> executados = new ArrayList<>();
        for (SagaStep<C> passo : passos) {
            try {
                passo.executar(contexto);
                executados.add(passo);
                auditor.aoExecutarPasso(sagaNome, passo.nome(), contexto);
            } catch (RuntimeException falhaOriginal) {
                log.warn("Saga '{}' falhou no passo '{}' — compensando {} passo(s) já executado(s) em ordem reversa",
                        sagaNome, passo.nome(), executados.size(), falhaOriginal);
                boolean compensacaoCompleta = compensarEmOrdemReversa(sagaNome, contexto, executados, auditor);
                auditor.aoFalhar(sagaNome, passo.nome(), contexto, falhaOriginal, compensacaoCompleta);
                throw falhaOriginal;
            }
        }
        auditor.aoConcluir(sagaNome, contexto);
    }

    private <C> boolean compensarEmOrdemReversa(String sagaNome, C contexto, List<SagaStep<C>> executados, SagaAuditor<C> auditor) {
        boolean completa = true;
        for (int i = executados.size() - 1; i >= 0; i--) {
            SagaStep<C> passo = executados.get(i);
            try {
                passo.compensar(contexto);
                auditor.aoCompensarPasso(sagaNome, passo.nome(), contexto);
            } catch (Exception falhaCompensacao) {
                completa = false;
                log.error("Saga '{}': falha ao compensar o passo '{}' — intervenção manual necessária", sagaNome, passo.nome(), falhaCompensacao);
                auditor.aoFalharCompensacao(sagaNome, passo.nome(), contexto, falhaCompensacao);
            }
        }
        return completa;
    }
}
