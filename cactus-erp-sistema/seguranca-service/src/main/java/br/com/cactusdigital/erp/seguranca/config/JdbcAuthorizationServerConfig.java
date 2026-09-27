package br.com.cactusdigital.erp.seguranca.config;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * As 3 tabelas oficiais do Spring Authorization Server (ver
 * V4__oauth2_authorization_server.sql) precisam de repositórios JDBC explícitos — sem
 * isso, o Spring usa implementações em memória, que perdem tudo (clients, tokens
 * emitidos) a cada restart do serviço.
 */
@Configuration
public class JdbcAuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(DataSource dataSource) {
        return new JdbcRegisteredClientRepository(new JdbcTemplate(dataSource));
    }

    /**
     * Achado real (2026-08-31), encontrado testando o logout de ponta a ponta no
     * navegador: {@code GET /connect/logout} devolvia 500 —
     * {@code "Could not resolve type id 'java.lang.Long' as a subtype of Object:
     * Configured PolymorphicTypeValidator denied resolution"}, na leitura de
     * {@code metadata.token.claims}. Causa: {@code AuthorizationServerConfig.jwtTokenCustomizer}
     * injeta claims próprias no token (`empresaId`/`usuarioId` como {@code Long},
     * `authorities` como {@code List<String>}), e o Spring Authorization Server persiste
     * as claims efetivas do token junto da {@code OAuth2Authorization} (pra
     * introspecção/revogação/logout). O {@code ObjectMapper}/{@code JsonMapper} padrão do
     * {@link JdbcOAuth2AuthorizationService} grava esses valores com tipo explícito
     * (`["java.lang.Long", 1]`, `["java.util.ImmutableCollections$ListN", [...]]` —
     * confirmado direto no banco) mas seu {@code PolymorphicTypeValidator} padrão não
     * autoriza resolver esses tipos JDK de volta na leitura — grava, mas não relê (achado
     * em duas rodadas: liberar só {@code Long} resolveu o primeiro erro, revelou o
     * segundo, com `ImmutableCollections$ListN` — sinal de que qualquer claim customizada
     * nova pode esbarrar num tipo JDK ainda não liberado). Corrigido liberando por PACOTE
     * (`java.lang.`/`java.util.`, via {@link BasicPolymorphicTypeValidator#allowIfSubType(String)})
     * em vez de tipo por tipo — cobre toda a família de tipos JDK que uma claims map
     * naturalmente carrega (Long, String, Boolean, List/Map/Set e suas variantes
     * imutáveis), sem liberar tudo (`Object.class`) igual seria o caso de não ter
     * restrição nenhuma. Aceitável aqui porque este {@code JsonMapper} só relê JSON que o
     * PRÓPRIO servidor escreveu no PRÓPRIO banco — não é desserialização de entrada
     * externa/não confiável, o cenário que motiva ter um allowlist restrito em primeiro
     * lugar. Mesmos módulos do Spring Security ({@link SecurityJacksonModules}, que já
     * inclui o módulo do Authorization Server quando presente no classpath) — usado tanto
     * pro row mapper (leitura) quanto pro parameters mapper (escrita), pra manter os dois
     * simétricos.
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(DataSource dataSource, RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService service =
                new JdbcOAuth2AuthorizationService(new JdbcTemplate(dataSource), registeredClientRepository);

        JsonMapper jsonMapper = authorizationJsonMapper();
        service.setAuthorizationRowMapper(
                new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(
                        registeredClientRepository, jsonMapper));
        service.setAuthorizationParametersMapper(
                new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationParametersMapper(jsonMapper));

        return service;
    }

    private JsonMapper authorizationJsonMapper() {
        List<JacksonModule> modules = SecurityJacksonModules.getModules(
                JdbcOAuth2AuthorizationService.class.getClassLoader(),
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("java.lang.")
                        .allowIfSubType("java.util."));
        return JsonMapper.builder().addModules(modules).build();
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(DataSource dataSource, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(new JdbcTemplate(dataSource), registeredClientRepository);
    }
}
