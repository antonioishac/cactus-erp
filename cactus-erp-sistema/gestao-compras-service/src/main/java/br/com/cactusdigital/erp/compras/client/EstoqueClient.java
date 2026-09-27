package br.com.cactusdigital.erp.compras.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.compras.exception.ComprasErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Chamada síncrona serviço-a-serviço do {@code gestao-compras-service} pro
 * {@code controle-estoque-service} — confirma recebimento de compra gerando
 * {@code ENTRADA}, uma chamada por item.
 * <p>
 * <b>Superada pela saga orquestrada (ver {@code RecebimentoCompraService#registrar}/
 * {@code br.com.cactusdigital.erp.common.saga.SagaOrchestrator})</b>: a limitação
 * originalmente registrada aqui ("sem saga/compensação — item 1..K-1 já debitados não são
 * revertidos") passou a ter uma compensação real — {@link #registrarSaida} é chamado
 * pela saga em ordem reversa se um passo POSTERIOR falhar, gerando uma {@code SAIDA}
 * real ({@code origemTipo=ESTORNO_COMPRA}) que desfaz a {@code ENTRADA} do mesmo item.
 * Mesmo espírito, mesmo padrão, de {@code vendas-service.EstoqueClient}. Continua valendo
 * o mesmo aviso já registrado no Javadoc de {@code SagaOrchestrator}: isto não é rollback
 * de banco, é uma nova operação de negócio compensatória — nem {@link #registrarEntrada}
 * nem {@link #registrarSaida} são idempotentes por si só.
 */
@Service
@RequiredArgsConstructor
public class EstoqueClient {

    private final RestClient estoqueRestClient;

    public void registrarEntrada(EstoqueMovimentoClientRequest request) {
        postMovimento(request, "Falha ao registrar entrada de estoque no controle-estoque-service: ");
    }

    /** Compensação de {@link #registrarEntrada} — gera {@code SAIDA} real (ledger append-only, nunca apaga o movimento original), usado só pela saga em {@code RecebimentoCompraService#registrar}. */
    public void registrarSaida(EstoqueMovimentoClientRequest request) {
        postMovimento(request, "Falha ao registrar saída de estoque (compensação) no controle-estoque-service: ");
    }

    private void postMovimento(EstoqueMovimentoClientRequest request, String mensagemErro) {
        try {
            estoqueRestClient.post()
                    .uri("/api/v1/movimentos")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new BusinessException(ComprasErrorCode.FALHA_INTEGRACAO_ESTOQUE, mensagemErro + ex.getMessage());
        }
    }
}
