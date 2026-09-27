package br.com.cactusdigital.erp.vendas.config;

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
 * — usado só pra confirmar venda (gera {@code SAIDA}). Mesmo padrão exato de
 * {@code gestao-compras-service.EstoqueClientConfig} — ver Javadoc de lá pro porquê de
 * {@code RestClient.Builder} precisar ser bean manual (modularização do Boot 4.1).
 * <p>
 * Ganhou um segundo destino (Fase 7 fechamento de pendência, 2026-09-04):
 * {@code financeiro-service}, também chamado ao confirmar venda, pra gerar as Contas a
 * Receber (1 por parcela) — fecha o gap registrado desde a criação desta fase
 * (`VendaCondicoesPagamento`/`Parcela` nunca geravam lançamento financeiro de verdade).
 * Registration própria (`financeiro-service`) com o MESMO client-id/secret de
 * `controle-estoque-service` (o client_id de serviço é sempre `vendas-service` —
 * authorities de destino diferentes já são resolvidas pelo allowlist do
 * `seguranca-service`, não por registration separada; a separação aqui é só
 * organizacional, uma registration por destino nomeado).
 * <p>
 * <b>Achado real corrigido ao adicionar o segundo destino</b>: os dois {@code @Bean}
 * de {@link RestClient} chamavam {@code .baseUrl(...).build()} em cima do MESMO
 * {@link RestClient.Builder} singleton (`restClientBuilder()`) — {@code Builder} é
 * mutável, então o segundo bean a rodar sobrescreveria o {@code baseUrl} do primeiro
 * antes dele terminar de montar (exatamente o risco já documentado em
 * {@code fiscal-service.CadastroEVendasClientConfig}, que usa um builder novo por
 * destino). Corrigido chamando {@code RestClient.builder()} fresco dentro de cada
 * método `@Bean` — nenhum builder compartilhado entre os dois.
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
