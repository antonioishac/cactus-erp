package br.com.cactusdigital.erp.sincronizacao.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.sincronizacao.dto.SaldoAgregadoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.exception.SincronizacaoErrorCode;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Só leitura — o push de {@code ESTOQUE_MOVIMENTO} não passa por aqui, é mensagem AMQP
 * (ver {@code OutboxRelayService}), não chamada REST. Esta classe existe só pra compor
 * o saldo agregado no pull de produto (ver {@code SincronizacaoPullService}).
 */
@Component
public class EstoqueClient {

    private final RestClient restClient;

    public EstoqueClient(RestClient estoqueRestClient) {
        this.restClient = estoqueRestClient;
    }

    public List<SaldoAgregadoSincronizacaoResponse> agregarPorEmpresaEProdutos(Long empresaId, List<Long> produtoIds) {
        if (produtoIds.isEmpty()) {
            return List.of();
        }
        try {
            String idsParam = produtoIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/saldos/agregado")
                            .queryParam("empresaId", empresaId)
                            .queryParam("produtoIds", idsParam)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SaldoAgregadoSincronizacaoResponse>>() {
                    });
        } catch (RestClientException ex) {
            throw new BusinessException(SincronizacaoErrorCode.FALHA_INTEGRACAO_ESTOQUE, ex.getMessage());
        }
    }
}
