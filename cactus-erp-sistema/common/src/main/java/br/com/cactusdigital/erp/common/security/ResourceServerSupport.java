package br.com.cactusdigital.erp.common.security;

import java.util.List;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

/**
 * Baseline comum de Resource Server (API REST protegida por JWT) que todo serviço do
 * Cactus ERP aplica na própria {@code SecurityFilterChain}: sem sessão (stateless — o
 * cliente sempre manda o Bearer token), CSRF desligado (não faz sentido em API REST
 * sem cookie de sessão), validação de JWT usando o {@link JwtAuthoritiesConverter}
 * compartilhado, e 401/403 no mesmo formato RFC 7807 do resto da API (ver
 * {@link ApiErrorAuthenticationEntryPoint}/{@link ApiErrorAccessDeniedHandler} — sem
 * isso, esses dois status saem sem o padrão de erro, porque são tratados pelo
 * {@code ExceptionTranslationFilter} antes da requisição chegar no controller advice).
 * <p>
 * Cada serviço chama {@link #applyBaseline(HttpSecurity, JwtAuthenticationConverter, ObjectMapper)}
 * dentro do próprio {@code @Bean SecurityFilterChain} e complementa com as regras de
 * autorização específicas dele (quais paths são públicos, etc) — não é um filtro chain
 * pronto, porque cada serviço tem necessidades diferentes (o cadastro-base, por
 * exemplo, também expõe os endpoints {@code /oauth2/**} do Authorization Server).
 */
public final class ResourceServerSupport {

    private ResourceServerSupport() {
    }

    public static HttpSecurity applyBaseline(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter, ObjectMapper objectMapper)
            throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((OAuth2ResourceServerConfigurer<HttpSecurity> oauth2) ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .exceptionHandling((ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(new ApiErrorAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new ApiErrorAccessDeniedHandler(objectMapper)));
    }

    /**
     * CORS pro {@code cactus-erp-web} (SPA, porta 5173 do Vite dev server) — sem isso
     * o navegador bloqueia toda chamada cross-origin do frontend antes mesmo de chegar
     * no Resource Server. Centralizado aqui (não em cada serviço individualmente)
     * porque {@link #applyBaseline} já é o ponto único de baseline de segurança
     * compartilhado que todo {@code SecurityFilterChain} do ecossistema chama.
     * <p>
     * <b>Público</b> (não só usado por {@link #applyBaseline}) porque o
     * {@code seguranca-service} tem uma SEGUNDA {@code SecurityFilterChain} própria
     * (a do Authorization Server em si — {@code /oauth2/**},
     * {@code /.well-known/openid-configuration} — Order HIGHEST_PRECEDENCE, matched via
     * {@code authorizationServer.getEndpointsMatcher()}) que não passa por
     * {@link #applyBaseline}. Sem aplicar este mesmo CORS lá também, a descoberta OIDC
     * que todo client (incluindo o `oidc-client-ts` do frontend) faz contra
     * {@code /.well-known/openid-configuration} antes de redirecionar pro login é
     * bloqueada pelo navegador — a resposta chega sem cabeçalho
     * {@code Access-Control-Allow-Origin}, mesmo o preflight OPTIONS respondendo OK
     * (achado real: o preflight passava, só a resposta real do GET não carregava o
     * cabeçalho, porque cada {@code SecurityFilterChain} do Spring Security tem sua
     * própria configuração de CORS independente).
     */
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
