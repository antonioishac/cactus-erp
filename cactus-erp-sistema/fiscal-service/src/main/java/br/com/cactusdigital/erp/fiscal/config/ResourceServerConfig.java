package br.com.cactusdigital.erp.fiscal.config;

import br.com.cactusdigital.erp.common.security.ResourceServerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

/**
 * Resource Server puro — mesmo padrão exato de todo serviço de negócio do monorepo (ver
 * {@code vendas-service.ResourceServerConfig}). Este serviço TAMBÉM é cliente OAuth2 de
 * outros dois serviços (ver {@code CadastroEVendasClientConfig}).
 */
@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter, ObjectMapper objectMapper) throws Exception {
        ResourceServerSupport.applyBaseline(http, jwtAuthenticationConverter, objectMapper);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
                        "/actuator/health", "/actuator/health/**", "/error")
                .permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}
