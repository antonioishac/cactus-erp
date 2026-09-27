package br.com.cactusdigital.erp.sincronizacao.config;

import br.com.cactusdigital.erp.common.client.ServiceOAuth2ClientSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

/**
 * O sincronizacao-service é cliente OAuth2 (Client Credentials) de TRÊS serviços —
 * mais que qualquer outro consumidor do monorepo até agora, porque é ele quem compõe o
 * pull de cadastro pro PDV (ver {@code SincronizacaoPullService}). Um
 * {@link OAuth2AuthorizedClientManager} único serve os três: a resolução de token é por
 * {@code registrationId} no momento da chamada (ver {@link ServiceOAuth2ClientSupport}),
 * não precisa de uma instância por serviço de destino.
 * <p>
 * Cada {@link RestClient} usa seu próprio {@code RestClient.builder()} fresco (não um
 * builder compartilhado) — um builder é mutável e {@code baseUrl(...)} grava estado nele;
 * reaproveitar a mesma instância entre os três apontaria os três clients pra última
 * base URL configurada.
 */
@Configuration
public class ClientesConfig {

    @Bean
    public OAuth2AuthorizedClientManager sincronizacaoAuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        return ServiceOAuth2ClientSupport.authorizedClientManager(clientRegistrationRepository, authorizedClientService);
    }

    @Bean
    public RestClient cadastroBaseRestClient(
            OAuth2AuthorizedClientManager sincronizacaoAuthorizedClientManager,
            @Value("${cactus.clients.cadastro-base-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), sincronizacaoAuthorizedClientManager, "cadastro-base-service")
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient estoqueRestClient(
            OAuth2AuthorizedClientManager sincronizacaoAuthorizedClientManager,
            @Value("${cactus.clients.controle-estoque-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), sincronizacaoAuthorizedClientManager, "controle-estoque-service")
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient segurancaRestClient(
            OAuth2AuthorizedClientManager sincronizacaoAuthorizedClientManager,
            @Value("${cactus.clients.seguranca-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), sincronizacaoAuthorizedClientManager, "seguranca-service")
                .baseUrl(baseUrl)
                .build();
    }
}
