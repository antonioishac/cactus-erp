package br.com.cactusdigital.erp.bi.client;

import br.com.cactusdigital.erp.bi.exception.BiErrorCode;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Única chamada síncrona serviço-a-serviço do {@code business-intelligence-service} —
 * busca a Curva ABC já calculada, pra não duplicar a lógica de classificação A/B/C aqui.
 */
@Service
@RequiredArgsConstructor
public class EstoqueClient {

    private final RestClient estoqueRestClient;

    public List<CurvaAbcItemClientResponse> buscarCurvaAbc(Long depositoId, int dias) {
        try {
            return estoqueRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/relatorios/curva-abc")
                            .queryParam("depositoId", depositoId)
                            .queryParam("dias", dias)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CurvaAbcItemClientResponse>>() {
                    });
        } catch (RestClientException ex) {
            throw new BusinessException(BiErrorCode.FALHA_INTEGRACAO_ESTOQUE,
                    "Falha ao buscar curva ABC no controle-estoque-service: " + ex.getMessage());
        }
    }
}
