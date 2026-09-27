package br.com.cactusdigital.erp.loja.config;

import br.com.cactusdigital.erp.common.security.ResourceServerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

/** Resource Server puro — mesmo padrão exato de todo serviço de negócio do monorepo. */
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
                // Carrinho/checkout é alcançado por comprador ANÔNIMO (visitante da loja,
                // sem conta staff) — o token do carrinho é a própria credencial de posse,
                // não uma authority OAuth2. Ver CLAUDE.md, "Fase 13, sub-fase 2 — decisão
                // de segurança". Pedido (o que nasce do checkout) fica fora deste permitAll,
                // atrás do RBAC de sempre — é trabalho de staff/lojista, não do comprador.
                .requestMatchers("/api/v1/carrinhos/**").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}
