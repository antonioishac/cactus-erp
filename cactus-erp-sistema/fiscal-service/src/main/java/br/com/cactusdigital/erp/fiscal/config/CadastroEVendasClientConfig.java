package br.com.cactusdigital.erp.fiscal.config;

import br.com.cactusdigital.erp.common.client.ServiceOAuth2ClientSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

/**
 * Este serviço é cliente OAuth2 (Client Credentials) do {@code vendas-service} e do
 * {@code cadastro-base-service} — usado só pra compor uma NF-e a partir de uma Venda
 * ({@code NotaFiscalEletronicaService#gerarDeVenda}). Uma única registration
 * ({@code fiscal-service}, ver {@code application.yml}) reaproveitada em 2
 * {@link RestClient} com {@code base-url} diferente — o token de {@code client_credentials}
 * não é por-destino, é só uma identidade de serviço com um conjunto fixo de authorities
 * (ver allowlist em {@code AuthorizationServerConfig.AUTORIDADES_POR_SERVICO} no
 * seguranca-service). Mesmo padrão de {@code vendas-service.EstoqueClientConfig}/
 * {@code gestao-compras-service.EstoqueClientConfig} — ver Javadoc de lá pro porquê de
 * {@code RestClient.Builder} precisar ser bean manual (modularização do Boot 4.1).
 */
@Configuration
public class CadastroEVendasClientConfig {

    private static final String REGISTRATION_ID = "fiscal-service";

    @Bean
    public OAuth2AuthorizedClientManager servicoAuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        return ServiceOAuth2ClientSupport.authorizedClientManager(clientRegistrationRepository, authorizedClientService);
    }

    // Cada RestClient usa seu próprio RestClient.builder() novo — não reaproveita um
    // Builder singleton entre os dois, pra não arriscar um mutar o baseUrl/interceptor
    // do outro antes do build() (RestClient.Builder é mutável).

    @Bean
    public RestClient vendasRestClient(
            OAuth2AuthorizedClientManager servicoAuthorizedClientManager,
            @Value("${cactus.clients.vendas-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), servicoAuthorizedClientManager, REGISTRATION_ID)
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient cadastroBaseRestClient(
            OAuth2AuthorizedClientManager servicoAuthorizedClientManager,
            @Value("${cactus.clients.cadastro-base-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), servicoAuthorizedClientManager, REGISTRATION_ID)
                .baseUrl(baseUrl)
                .build();
    }

    /** Fase 9 (NFS-e) — lê a OsAbertura no os-service pra compor a nota de serviço. */
    @Bean
    public RestClient osRestClient(
            OAuth2AuthorizedClientManager servicoAuthorizedClientManager,
            @Value("${cactus.clients.os-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), servicoAuthorizedClientManager, REGISTRATION_ID)
                .baseUrl(baseUrl)
                .build();
    }

    /** Fase 13, sub-fase 3 (NFC-e) — lê Loja/Pedido no loja-virtual-service pra compor a nota de consumidor. */
    @Bean
    public RestClient lojaVirtualRestClient(
            OAuth2AuthorizedClientManager servicoAuthorizedClientManager,
            @Value("${cactus.clients.loja-virtual-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), servicoAuthorizedClientManager, REGISTRATION_ID)
                .baseUrl(baseUrl)
                .build();
    }
}
