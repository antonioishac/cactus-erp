package br.com.cactusdigital.erp.bi.config;

import br.com.cactusdigital.erp.common.client.ServiceOAuth2ClientSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

/**
 * Este serviço é cliente OAuth2 (Client Credentials) do {@code controle-estoque-service}
 * — usado só pra buscar a Curva ABC já calculada (evita duplicar a classificação A/B/C
 * em SQL bruto aqui). Mesmo padrão da Fase 3 (`gestao-compras-service.EstoqueClientConfig`).
 */
@Configuration
public class EstoqueClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public OAuth2AuthorizedClientManager estoqueAuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        return ServiceOAuth2ClientSupport.authorizedClientManager(clientRegistrationRepository, authorizedClientService);
    }

    @Bean
    public RestClient estoqueRestClient(
            RestClient.Builder builder,
            OAuth2AuthorizedClientManager estoqueAuthorizedClientManager,
            @Value("${cactus.clients.controle-estoque-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(builder, estoqueAuthorizedClientManager, "controle-estoque-service")
                .baseUrl(baseUrl)
                .build();
    }
}
