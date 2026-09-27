package br.com.cactusdigital.erp.common.client;

import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

/**
 * Suporte pra um serviço agir como <b>cliente</b> OAuth2 de outro serviço interno via
 * Client Credentials — chamada de máquina-a-máquina, sem usuário por trás. Primeiro
 * consumidor: {@code gestao-compras-service} chamando {@code POST /api/v1/movimentos}
 * no {@code controle-estoque-service} ao confirmar um recebimento de compra.
 * <p>
 * Usa {@link AuthorizedClientServiceOAuth2AuthorizedClientManager} — a variante
 * <b>não vinculada a uma requisição HTTP</b> de entrada, diferente do
 * {@code DefaultOAuth2AuthorizedClientManager} que o Spring Boot autoconfigura pensando
 * num fluxo web (ele exige um {@code HttpServletRequest}/principal autenticado no
 * contexto). Aqui a chamada acontece dentro da lógica de negócio do próprio serviço,
 * sem requisição de usuário disparando-a diretamente — o serviço autentica como si
 * mesmo, não em nome de ninguém.
 * <p>
 * Cada serviço consumidor declara o próprio registration em
 * {@code spring.security.oauth2.client.registration.<id>} (Spring Boot autoconfigura
 * {@link ClientRegistrationRepository}/{@link OAuth2AuthorizedClientService} a partir
 * disso) e usa {@link #authorizedClientManager} + {@link #applyBearerToken} pra montar
 * o próprio {@link RestClient} apontando pro serviço de destino.
 */
public final class ServiceOAuth2ClientSupport {

    private ServiceOAuth2ClientSupport() {
    }

    /**
     * Manager de Client Credentials pra uso fora do contexto de uma requisição HTTP de
     * entrada. Cada serviço expõe isso como {@code @Bean} usando os
     * {@link ClientRegistrationRepository}/{@link OAuth2AuthorizedClientService}
     * autoconfigurados pelo Spring Boot a partir do {@code application.yml}.
     */
    public static OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }

    /**
     * Anexa ao {@link RestClient.Builder} um interceptor que resolve e injeta o Bearer
     * token do {@code registrationId} informado em toda requisição — o token é
     * obtido/renovado automaticamente pelo {@code authorizedClientManager} (cacheado
     * até expirar, então não gera um round-trip de token a cada chamada).
     */
    public static RestClient.Builder applyBearerToken(
            RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager, String registrationId) {
        OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        interceptor.setClientRegistrationIdResolver(request -> registrationId);
        return builder.requestInterceptor(interceptor);
    }
}
