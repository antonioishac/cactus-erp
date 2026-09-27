package br.com.cactusdigital.erp.seguranca.config;

import br.com.cactusdigital.erp.common.security.ResourceServerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import tools.jackson.databind.ObjectMapper;

/**
 * Chain "catch-all" (roda depois da do {@link AuthorizationServerConfig}, que só pega
 * {@code /oauth2/**}): serve a tela de login (form) que o fluxo Authorization Code
 * redireciona o navegador quando não há sessão ainda, E protege a própria API REST
 * deste serviço ({@code /api/v1/**}) como Resource Server — o seguranca-service valida
 * o próprio token exatamente como os outros serviços validam (mesmo
 * {@link ResourceServerSupport} compartilhado via {@code common}).
 * <p>
 * A página HTML em si é servida por
 * {@link br.com.cactusdigital.erp.seguranca.controller.LoginPageController} (com a
 * marca do Cactus ERP) — não pelo {@code DefaultLoginPageGeneratingFilter} genérico do
 * Spring Security, que chegou a ser registrado manualmente aqui numa passada anterior
 * (junto de {@code DefaultResourcesFilter}, pro CSS dele) só pra contornar esses dois
 * filtros não se auto-registrarem nesta chain — removido de vez quando a página própria
 * (2026-08-31) tornou esses workarounds desnecessários. Histórico completo desses dois
 * achados fica só no `CLAUDE.md` raiz, não repetido aqui.
 */
@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter, ObjectMapper objectMapper) throws Exception {
        ResourceServerSupport.applyBaseline(http, jwtAuthenticationConverter, objectMapper);

        // Achado real #0 (2026-08-31), causa raiz do achado #2 abaixo (testado e confirmado
        // SEPARADO do achado #1 — remover o workaround do #1 sem este fix não resolvia
        // GET /login, então são bugs independentes, não a mesma causa): applyBaseline() seta
        // SessionCreationPolicy.STATELESS (correto pros outros 5 serviços, puramente API
        // REST/JWT) — mas ESTE chain é o único que também hospeda .formLogin() (fluxo humano
        // de login, ver Javadoc da classe). STATELESS faz o Spring Security trocar o
        // RequestCache e o SecurityContextRepository compartilhados desta chain por
        // implementações NULAS (nunca leem/escrevem na HttpSession) — então mesmo com um
        // login bem-sucedido, o SecurityContext nunca sobrevivia até a requisição seguinte:
        // o navegador, ao ser redirecionado de volta pro /oauth2/authorize original, chegava
        // lá "deslogado de novo" e caía num loop de volta pro /login. Restaurado aqui pra
        // IF_REQUIRED (o padrão do Spring Security) só nesta chain — as chamadas de API
        // autenticadas por JWT continuam sem criar sessão nenhuma na prática (Bearer token
        // não usa HttpSession), então nada muda pra elas.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                                "/actuator/health", "/actuator/health/**", "/error",
                                // Atalho de login só de desenvolvimento (DevAuthController, @Profile("!prod"),
                                // nem existe fora de dev) — precisa ser público, é ele que EMITE o token.
                                "/api/v1/auth/dev-login",
                                // Página HTML de login (GET) + processamento do form (POST) — pro fluxo
                                // Authorization Code redirecionar aqui quando não há sessão ainda. Explícito
                                // aqui (não só confiando no auto-permitAll implícito do .formLogin()) porque
                                // applyBaseline() já setou um AuthenticationEntryPoint único (JSON, pra API
                                // REST) que teria prioridade sobre esse auto-permitAll.
                                "/login")
                        .permitAll()
                        .anyRequest().authenticated())
                // Achado real #2 (2026-08-31, testando o login end-to-end), consequência
                // direta do #0 acima: com o chain STATELESS, o RequestCache compartilhado
                // virava NullRequestCache, então o redirect pós-login sempre ia pra "/" em
                // vez de voltar pro /oauth2/authorize salvo. Explícito aqui como cinto e
                // suspensório mesmo já corrigido o #0 — usa a própria HttpSessionRequestCache
                // (default do construtor) em vez de depender do RequestCache compartilhado
                // da chain, então continua correto mesmo se a política de sessão mudar nesta
                // chain de novo no futuro.
                .formLogin(form -> form.successHandler(new SavedRequestAwareAuthenticationSuccessHandler()));

        return http.build();
    }
}
