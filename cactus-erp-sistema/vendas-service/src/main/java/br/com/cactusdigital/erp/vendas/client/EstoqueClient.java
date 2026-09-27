package br.com.cactusdigital.erp.vendas.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.vendas.exception.VendasErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Chamada síncrona serviço-a-serviço do {@code vendas-service} pro
 * {@code controle-estoque-service} — confirma venda gerando {@code SAIDA}, uma chamada
 * por item.
 * <p>
 * <b>Superada pela saga orquestrada (ver {@code VendaService#confirmar}/
 * {@code br.com.cactusdigital.erp.common.saga.SagaOrchestrator})</b>: a limitação
 * originalmente registrada aqui ("sem saga/compensação — item 1..K-1 já debitados não são
 * revertidos") passou a ter uma compensação real — {@link #registrarEntrada} é chamado
 * pela saga em ordem reversa se um passo POSTERIOR falhar, gerando uma {@code ENTRADA}
 * real ({@code origemTipo=ESTORNO_VENDA}) que desfaz a {@code SAIDA} do mesmo item.
 * Continua valendo o mesmo aviso já registrado no Javadoc de {@code SagaOrchestrator}:
 * isto não é rollback de banco, é uma nova operação de negócio compensatória — nem
 * {@link #registrarSaida} nem {@link #registrarEntrada} são idempotentes por si só.
 * Mesma limitação, mesmo padrão, ainda não aplicado em
 * {@code gestao-compras-service.EstoqueClient} (ver BACKLOG.md).
 */
@Service
@RequiredArgsConstructor
public class EstoqueClient {

    private final RestClient estoqueRestClient;

    public void registrarSaida(EstoqueMovimentoClientRequest request) {
        postMovimento(request, "Falha ao registrar saída de estoque no controle-estoque-service: ");
    }

    /** Compensação de {@link #registrarSaida} — gera {@code ENTRADA} real (ledger append-only, nunca apaga o movimento original), usado só pela saga em {@code VendaService#confirmar}. */
    public void registrarEntrada(EstoqueMovimentoClientRequest request) {
        postMovimento(request, "Falha ao registrar entrada de estoque (compensação) no controle-estoque-service: ");
    }

    private void postMovimento(EstoqueMovimentoClientRequest request, String mensagemErro) {
        try {
            estoqueRestClient.post()
                    .uri("/api/v1/movimentos")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(VendasErrorCode.FALHA_INTEGRACAO_ESTOQUE, mensagemErro + ex.getMessage());
        }
    }
}
