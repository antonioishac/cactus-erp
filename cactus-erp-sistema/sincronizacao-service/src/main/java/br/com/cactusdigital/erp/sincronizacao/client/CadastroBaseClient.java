package br.com.cactusdigital.erp.sincronizacao.client;

import br.com.cactusdigital.erp.sincronizacao.dto.ClienteSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ConfiguracaoLojaSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.FormaPagamentoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ProdutoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.TabelaPrecoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.UpstreamPage;
import br.com.cactusdigital.erp.sincronizacao.exception.SincronizacaoErrorCode;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import java.time.Instant;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Chamadas de leitura ao {@code cadastro-base-service} pra compor o pull de cadastro
 * (produto, cliente, tabela de preço, forma de pagamento, config de loja). Todos os
 * corpos de resposta são desserializados direto nos DTOs deste módulo (mirror), não
 * nos DTOs do cadastro-base-service — propriedades desconhecidas são ignoradas pelo
 * Jackson por padrão, então os DTOs daqui podem ser um subconjunto dos campos de lá.
 */
@Component
public class CadastroBaseClient {

    private final RestClient restClient;

    public CadastroBaseClient(RestClient cadastroBaseRestClient) {
        this.restClient = cadastroBaseRestClient;
    }

    public UpstreamPage<ProdutoSincronizacaoResponse> listarProdutos(Instant desde, int pagina, int tamanho) {
        return get("/api/v1/produtos", desde, pagina, tamanho, new ParameterizedTypeReference<>() {
        });
    }

    public UpstreamPage<ClienteSincronizacaoResponse> listarClientes(Instant desde, int pagina, int tamanho) {
        return get("/api/v1/clientes", desde, pagina, tamanho, new ParameterizedTypeReference<>() {
        });
    }

    public UpstreamPage<TabelaPrecoSincronizacaoResponse> listarTabelasPreco(Instant desde, int pagina, int tamanho) {
        return get("/api/v1/tabelas-precos", desde, pagina, tamanho, new ParameterizedTypeReference<>() {
        });
    }

    public UpstreamPage<FormaPagamentoSincronizacaoResponse> listarFormasPagamento(Instant desde, int pagina, int tamanho) {
        return get("/api/v1/formas-pagamento", desde, pagina, tamanho, new ParameterizedTypeReference<>() {
        });
    }

    public ConfiguracaoLojaSincronizacaoResponse buscarConfiguracaoLoja(Long empresaId) {
        try {
            return restClient.get()
                    .uri("/api/v1/empresas/{id}/configuracao-operacional", empresaId)
                    .retrieve()
                    .body(ConfiguracaoLojaSincronizacaoResponse.class);
        } catch (RestClientException ex) {
            throw new BusinessException(SincronizacaoErrorCode.FALHA_INTEGRACAO_CADASTRO_BASE, ex.getMessage());
        }
    }

    private <T> UpstreamPage<T> get(String path, Instant desde, int pagina, int tamanho, ParameterizedTypeReference<UpstreamPage<T>> tipo) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path(path)
                            .queryParam("page", pagina)
                            .queryParam("size", tamanho)
                            .queryParamIfPresent("desde", Optional.ofNullable(desde))
                            .build())
                    .retrieve()
                    .body(tipo);
        } catch (RestClientException ex) {
            throw new BusinessException(SincronizacaoErrorCode.FALHA_INTEGRACAO_CADASTRO_BASE, ex.getMessage());
        }
    }
}
