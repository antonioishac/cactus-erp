package br.com.cactusdigital.erp.sincronizacao.client;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.sincronizacao.dto.OperadorSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.exception.SincronizacaoErrorCode;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Composição pro pull de operador (Fase 6) — todo o dado já vem pronto de lá, ver
 * Javadoc de {@code OperadorSincronizacaoService} no seguranca-service. */
@Component
public class SegurancaClient {

    private final RestClient restClient;

    public SegurancaClient(RestClient segurancaRestClient) {
        this.restClient = segurancaRestClient;
    }

    public List<OperadorSincronizacaoResponse> listarOperadoresPorEmpresa(Long empresaId) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/operadores").queryParam("empresaId", empresaId).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<OperadorSincronizacaoResponse>>() {
                    });
        } catch (RestClientException ex) {
            throw new BusinessException(SincronizacaoErrorCode.FALHA_INTEGRACAO_SEGURANCA, ex.getMessage());
        }
    }
}
