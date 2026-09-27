package br.com.cactusdigital.erp.seguranca.config;

import br.com.cactusdigital.erp.common.security.ResourceServerSupport;
import br.com.cactusdigital.erp.seguranca.controller.AuthController;
import br.com.cactusdigital.erp.seguranca.service.TokenClaimsService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * O seguranca-service É o Authorization Server (OAuth2/JWT) do ecossistema — os demais
 * serviços (incluindo o cadastro-base-service, que hospedava isso antes desta extração)
 * só validam token contra o {@code /oauth2/jwks} exposto aqui.
 * <p>
 * Definir esta {@link SecurityFilterChain} (e a de {@link ResourceServerConfig})
 * explicitamente faz o Spring Boot 4 desistir da autoconfiguração equivalente dele
 * ({@code OAuth2AuthorizationServerWebSecurityConfiguration}, que só entra em ação
 * via {@code @ConditionalOnMissingBean(SecurityFilterChain.class)}) — necessário
 * porque a versão autoconfigurada do Boot não protege a API de negócio deste próprio
 * serviço como Resource Server, só faz {@code formLogin}.
 */
@Configuration
public class AuthorizationServerConfig {

    /**
     * Allowlist mínima e deliberada de authorities concedidas a token de serviço
     * (Client Credentials), por {@code clientId} — evolui pra uma tabela
     * ({@code funcionalidade_servico}, mesmo padrão de {@code UsuarioPapelEmpresa}) se o
     * número de integrações crescer a ponto de um mapa hardcoded virar difícil de
     * manter — não antes disso.
     * <p>
     * {@code sincronizacao-service} (Fase 6) é o client com mais authorities do mapa —
     * é a camada anticorrupção que compõe o pull de cadastro pro PDV, então precisa de
     * leitura nos 3 serviços de origem (produto+saldo agregado, cliente/tabela de
     * preço/forma de pagamento/config de loja, operador); ele NÃO recebe
     * {@code ESTOQUE_MOVIMENTAR} — o push de estoque não é uma chamada REST dele pro
     * controle-estoque-service, é uma mensagem AMQP consumida lá dentro (ver
     * {@code EstoqueSincronizacaoListener}), sem token envolvido nesse trecho.
     */
    private static final Map<String, List<String>> AUTORIDADES_POR_SERVICO = Map.of(
            // FINANCEIRO_LANCAR (2026-09-05, fechamento de pendência do financeiro-service):
            // gera Contas a Pagar no financeiro-service quando um PedidoCompra vira
            // RECEBIDO_TOTAL, mesmo padrão exato de vendas-service -> financeiro-service.
            "gestao-compras-service", List.of("ESTOQUE_MOVIMENTAR", "ESTOQUE_CONSULTAR", "FINANCEIRO_LANCAR"),
            "controle-estoque-service", List.of(),
            "business-intelligence-service", List.of("ESTOQUE_CONSULTAR"),
            "sincronizacao-service", List.of(
                    "CADASTRO_PRODUTO_CONSULTAR", "CADASTRO_CLIENTE_CONSULTAR", "CADASTRO_EMPRESA_CONSULTAR",
                    "ESTOQUE_CONSULTAR", "SEGURANCA_CONSULTAR"),
            // vendas-service (Fase 7) confirma venda -> baixa estoque real (SAIDA), mesmo
            // padrão exato de gestao-compras-service -> controle-estoque-service.
            // FINANCEIRO_LANCAR (2026-09-04, fechamento de pendência da Fase 7): gera
            // Contas a Receber no financeiro-service ao confirmar uma venda.
            "vendas-service", List.of("ESTOQUE_MOVIMENTAR", "ESTOQUE_CONSULTAR", "FINANCEIRO_LANCAR"),
            // fiscal-service (Fase 8) compõe NF-e a partir de uma Venda: lê venda
            // (vendas-service) e cliente/produto/empresa/transportadora (cadastro-base),
            // e ESCREVE em CADASTRO_EMPRESA_EDITAR só pra reservar/incrementar o próximo
            // número de NF-e (EmpresaConfiguracaoFiscalService#reservarProximoNumeroNfe).
            // Fase 9 (NFS-e) adicionou OS_CONSULTAR — fiscal-service também lê a OsAbertura
            // no os-service pra compor a nota de serviço.
            "fiscal-service", List.of("VENDAS_CONSULTAR", "CADASTRO_CLIENTE_CONSULTAR",
                    "CADASTRO_PRODUTO_CONSULTAR", "CADASTRO_EMPRESA_CONSULTAR", "CADASTRO_EMPRESA_EDITAR",
                    "CADASTRO_TRANSPORTADORA_CONSULTAR", "OS_CONSULTAR", "LOJA_CONSULTAR"));

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2AuthorizationServer(authorizationServer -> {
            http.securityMatcher(authorizationServer.getEndpointsMatcher());
            authorizationServer.oidc(Customizer.withDefaults());
        });

        // Mesma CORS de ResourceServerSupport.applyBaseline — precisa ser reaplicado
        // aqui porque esta é uma SecurityFilterChain separada (Order HIGHEST_PRECEDENCE,
        // matched só nos endpoints do Authorization Server) que não passa por
        // applyBaseline. Sem isso, a descoberta OIDC (/.well-known/openid-configuration)
        // que todo client faz antes do redirect de login é bloqueada pelo navegador —
        // ver Javadoc de ResourceServerSupport.corsConfigurationSource().
        http.cors(cors -> cors.configurationSource(ResourceServerSupport.corsConfigurationSource()));

        // Renovação silenciosa de sessão do cactus-erp-web (client público, sem
        // refresh_token — Spring Authorization Server não emite pra client_authentication
        // _method=none, ver cactus-erp-web/CLAUDE.md, pendência 0): o padrão OIDC pra
        // isso é um iframe oculto batendo em /oauth2/authorize?prompt=none, que só
        // funciona se o navegador permitir esse endpoint dentro de um iframe. O default
        // do Spring Security (X-Frame-Options: DENY, aplicado a toda SecurityFilterChain
        // que não desliga explicitamente) bloquearia isso. Relaxado só NESTA chain
        // (matched só nos endpoints do Authorization Server, nunca na API de negócio dos
        // outros 5 serviços) e só para as origens conhecidas do próprio frontend — via
        // CSP frame-ancestors (mecanismo moderno, permite allowlist de múltiplas
        // origens; X-Frame-Options não suporta isso, só SAMEORIGIN/DENY/uma única origem
        // via ALLOW-FROM, que é obsoleto e não é respeitado por navegadores atuais).
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "frame-ancestors 'self' http://localhost:5173 http://127.0.0.1:5173")));

        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
        http.oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
        http.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"), htmlRequestMatcher()));

        return http.build();
    }

    private static RequestMatcher htmlRequestMatcher() {
        MediaTypeRequestMatcher requestMatcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        requestMatcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));
        return requestMatcher;
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${cactus.security.issuer-uri:http://localhost:8082}") String issuerUri) {
        return AuthorizationServerSettings.builder()
                .issuer(issuerUri)
                .build();
    }

    /**
     * Injeta no JWT (só no access token, não no id_token OIDC) as claims de negócio
     * que os demais serviços precisam pra autorizar: authorities já resolvidas pra
     * empresa ativa, usuarioId, login, empresaId. Ver {@link TokenClaimsService} pra
     * como a empresa ativa é escolhida no login (a primeira que o usuário tem acesso) e
     * como trocar depois ({@code AuthController#trocarEmpresa}). Não carrega
     * {@code empresaNome} — ver Javadoc de {@code TokenClaimsService.ClaimsResolvidas}.
     * <p>
     * Em Client Credentials o principal é o próprio client (ex:
     * {@code gestao-compras-service}), não um {@code Usuario}, então não existe
     * empresa/papel pra resolver via {@link TokenClaimsService} — o token de serviço
     * ganha uma claim {@code authorities} própria, resolvida a partir de
     * {@link #AUTORIDADES_POR_SERVICO} pelo {@code clientId}, não pelo RBAC de usuário
     * ({@code Papel}/{@code UsuarioPapelEmpresa}). Sem essa claim, um token de serviço
     * não passaria em nenhum {@code @PreAuthorize("hasAuthority(...)")} dos outros
     * serviços — foi exatamente essa lacuna que impedia {@code gestao-compras-service}
     * de chamar {@code POST /api/v1/movimentos} no {@code controle-estoque-service} ao
     * confirmar um recebimento de compra.
     * <p>
     * <b>Client "um por loja" (Fase 6, 2026-08-31)</b>: um client provisionado via
     * {@code ClienteLojaService} carrega {@code lojaId} no {@code ClientSettings}
     * customizado do {@code RegisteredClient} — quando presente, o token ganha a claim
     * {@code lojaId} em vez de entrar no fluxo de {@link #AUTORIDADES_POR_SERVICO} (esse
     * client não chama outro serviço do ecossistema, só o próprio
     * {@code sincronizacao-service}, que valida {@code lojaId} contra o parâmetro de
     * cada chamada — ver {@code FASE6_SINCRONIZACAO_PDV.md}, seção 11.3). Sem
     * authorities específicas: o isolamento é inteiramente por {@code lojaId}, não por
     * papel/authority.
     * <p>
     * <b>Limitação conhecida (decisão registrada, não bug escondido)</b>: em
     * {@code AuthorizationGrantType.REFRESH_TOKEN} este customizer roda de novo e
     * chama {@code resolverEmpresaPadrao} de novo — ou seja, um token renovado pelo
     * fluxo {@code refresh_token} sempre volta pra empresa padrão, mesmo que o usuário
     * tivesse trocado de empresa via {@code trocarEmpresa} antes de o token original
     * expirar (esse endpoint mint um JWT à parte, sem {@code OAuth2Authorization}
     * persistido, então esta customização não tem como saber que a troca aconteceu).
     * Front-end precisa lidar com isso client-side: guardar qual empresa está ativa e
     * chamar {@code trocarEmpresa} de novo sempre que for operar fora da empresa padrão
     * depois de um refresh (ver CLAUDE.md, seção "Sessão multi-empresa e refresh_token").
     * Persistir a empresa ativa no {@code OAuth2Authorization} (coluna {@code attributes})
     * resolveria isso do lado do servidor — decisão de não fazer isso agora, registrada.
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(TokenClaimsService tokenClaimsService) {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return;
            }
            if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
                String lojaIdSetting = context.getRegisteredClient().getClientSettings().getSetting("lojaId");
                if (lojaIdSetting != null) {
                    context.getClaims().claim("lojaId", Long.valueOf(lojaIdSetting));
                    context.getClaims().claim("authorities", List.of());
                    return;
                }
                String clientId = context.getRegisteredClient().getClientId();
                List<String> authorities = AUTORIDADES_POR_SERVICO.getOrDefault(clientId, List.of());
                context.getClaims().claim("authorities", authorities);
                return;
            }
            String login = context.getPrincipal().getName();
            TokenClaimsService.ClaimsResolvidas claims = tokenClaimsService.resolverEmpresaPadrao(login);

            context.getClaims().claims(claimsMap -> {
                claimsMap.put("authorities", List.copyOf(claims.authorities()));
                claimsMap.put("usuarioId", claims.usuarioId());
                claimsMap.put("login", claims.login());
                if (claims.empresaId() != null) {
                    claimsMap.put("empresaId", claims.empresaId());
                }
            });
        };
    }
}
