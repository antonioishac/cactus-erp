package br.com.cactusdigital.erp.seguranca.config;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * Semeia os clients OAuth2 via código (não SQL — ver comentário no topo de
 * V5__seed_dados.sql) usando o próprio {@link RegisteredClientRepository}, o que
 * garante a serialização correta de {@code client_settings}/{@code token_settings}.
 * Idempotente: só cria se o {@code clientId} ainda não existir, então roda seguro em
 * todo restart.
 */
@Component
@RequiredArgsConstructor
class OAuth2ClientSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientSeeder.class);

    /** Segredo de DESENVOLVIMENTO LOCAL apenas — trocar por secret gerado/gerenciado antes de qualquer outro ambiente. */
    private static final String SEGREDO_DEV_SERVICO = "cactus-service-secret-local-dev";

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        registrarOuAtualizarRedirectUris(clientWeb());
        registrarSeNaoExistir(clientServico("controle-estoque-service"));
        registrarSeNaoExistir(clientServico("gestao-compras-service"));
        registrarSeNaoExistir(clientServico("business-intelligence-service"));
        registrarSeNaoExistir(clientServico("sincronizacao-service"));
        registrarSeNaoExistir(clientServico("vendas-service"));
        registrarSeNaoExistir(clientServico("fiscal-service"));
    }

    private void registrarSeNaoExistir(RegisteredClient client) {
        if (registeredClientRepository.findByClientId(client.getClientId()) != null) {
            return;
        }
        registeredClientRepository.save(client);
        log.info("Client OAuth2 semeado: {}", client.getClientId());
    }

    /**
     * {@code clientWeb()} é o único client cujo conjunto de {@code redirectUris} muda
     * com alguma frequência à medida que o frontend ganha rotas novas (ex:
     * {@code /silent-renew}, adicionado pra fechar a pendência de renovação silenciosa
     * sem {@code refresh_token} — ver {@code cactus-erp-web/CLAUDE.md}, pendência 0) —
     * diferente dos clients de serviço (`clientServico`, fixos) e dos clients de loja
     * (dinâmicos, gerenciados por {@code ClienteLojaService}, nunca tocados aqui). Sem
     * este merge, um restart não propagaria uma rota nova pro client já persistido, e
     * o Authorization Server rejeitaria qualquer `redirect_uri`/`silent_redirect_uri`
     * que o código já conhece mas o banco ainda não.
     */
    private void registrarOuAtualizarRedirectUris(RegisteredClient client) {
        RegisteredClient existente = registeredClientRepository.findByClientId(client.getClientId());
        if (existente == null) {
            registeredClientRepository.save(client);
            log.info("Client OAuth2 semeado: {}", client.getClientId());
            return;
        }
        if (existente.getRedirectUris().equals(client.getRedirectUris())) {
            return;
        }
        RegisteredClient atualizado = RegisteredClient.from(existente)
                .redirectUris(uris -> {
                    uris.clear();
                    uris.addAll(client.getRedirectUris());
                })
                .build();
        registeredClientRepository.save(atualizado);
        log.info("Client OAuth2 '{}' — redirectUris atualizadas: {}", client.getClientId(), client.getRedirectUris());
    }

    /** Frontend web do ERP (fora deste repositório) — Authorization Code + PKCE, client público (sem secret). */
    private RegisteredClient clientWeb() {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("cactus-erp-web")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:5173/callback")
                .redirectUri("http://127.0.0.1:5173/callback")
                // Alvo do iframe oculto de renovação silenciosa (prompt=none) — ver
                // SilentRenewPage.tsx/authClient.ts no cactus-erp-web e a pendência 0
                // do CLAUDE.md daquele projeto (sem isso, o Authorization Server rejeita
                // o redirect_uri do request feito por dentro do iframe).
                .redirectUri("http://localhost:5173/silent-renew")
                .redirectUri("http://127.0.0.1:5173/silent-renew")
                .postLogoutRedirectUri("http://localhost:5173/logout")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(true)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofHours(12))
                        .reuseRefreshTokens(false)
                        .build())
                .build();
    }

    /** Client máquina-a-máquina (Client Credentials) — um por serviço consumidor, ver roadmap de sincronização. */
    private RegisteredClient clientServico(String clientId) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(SEGREDO_DEV_SERVICO))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("internal")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15))
                        .build())
                .build();
    }
}
