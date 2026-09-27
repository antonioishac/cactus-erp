package br.com.cactusdigital.erp.compras.config;

import br.com.cactusdigital.erp.common.client.ServiceOAuth2ClientSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

/**
 * Este serviço é cliente OAuth2 (Client Credentials) de dois destinos: o
 * {@code controle-estoque-service} (confirmar recebimento de compra, gera
 * {@code ENTRADA}) e o {@code financeiro-service} (gerar Contas a Pagar quando um
 * {@code PedidoCompra} vira {@code RECEBIDO_TOTAL}, fechamento de pendência 2026-09-05).
 * O {@link ClientRegistrationRepository}/{@link OAuth2AuthorizedClientService} são
 * autoconfigurados pelo Spring Boot a partir de
 * {@code spring.security.oauth2.client.registration.*} no {@code application.yml}; só o
 * manager e os {@link RestClient} são beans manuais.
 * <p>
 * <b>Cada {@link RestClient} usa um {@code RestClient.builder()} FRESCO, nunca um bean
 * {@code RestClient.Builder} compartilhado</b> — achado real ao adicionar este segundo
 * destino no `vendas-service` (mesma dupla estoque+financeiro): {@code RestClient.Builder}
 * é mutável, então dois beans `@Bean` chamando `.baseUrl(...).build()` em cima do MESMO
 * builder singleton fazem o segundo sobrescrever o `baseUrl` do primeiro antes dele
 * terminar de montar. Builder fresco por destino evita a colisão — mesmo padrão já usado
 * em {@code fiscal-service.CadastroEVendasClientConfig}.
 */
@Configuration
public class EstoqueClientConfig {

    @Bean
    public OAuth2AuthorizedClientManager estoqueAuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        return ServiceOAuth2ClientSupport.authorizedClientManager(clientRegistrationRepository, authorizedClientService);
    }

    @Bean
    public RestClient estoqueRestClient(
            OAuth2AuthorizedClientManager estoqueAuthorizedClientManager,
            @Value("${cactus.clients.controle-estoque-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), estoqueAuthorizedClientManager, "controle-estoque-service")
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient financeiroRestClient(
            OAuth2AuthorizedClientManager estoqueAuthorizedClientManager,
            @Value("${cactus.clients.financeiro-service.base-url}") String baseUrl) {
        return ServiceOAuth2ClientSupport.applyBearerToken(RestClient.builder(), estoqueAuthorizedClientManager, "financeiro-service")
                .baseUrl(baseUrl)
                .build();
    }
}
