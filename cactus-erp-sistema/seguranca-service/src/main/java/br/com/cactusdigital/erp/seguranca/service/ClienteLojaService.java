package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.seguranca.dto.ClienteLojaRequest;
import br.com.cactusdigital.erp.seguranca.dto.ClienteLojaResponse;
import br.com.cactusdigital.erp.seguranca.dto.ClienteLojaResumoResponse;
import br.com.cactusdigital.erp.seguranca.exception.SegurancaErrorCode;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

/**
 * Provisiona/lista clients OAuth2 Client Credentials "um por loja" (decisão confirmada
 * na seção 9.2 do FASE6_SINCRONIZACAO_PDV.md, infraestrutura fechada em 2026-08-31 pra
 * corrigir o gap de isolamento por loja registrado na seção 11.3). Um client por
 * {@code empresaId} (= loja/filial no cadastro-base-service — referência solta, sem FK,
 * mesmo padrão cross-serviço já usado em {@code Usuario.colaboradorId}), {@code clientId}
 * derivado deterministicamente (evita duas lojas ganharem client duplicado por engano).
 * <p>
 * {@code lojaId} é gravado como {@code String} no {@link ClientSettings} customizado do
 * {@link RegisteredClient} (não {@code Long}) de propósito: o {@code JsonMapper} interno
 * do {@code JdbcRegisteredClientRepository} usado por baixo dos panos tem o mesmo tipo de
 * {@code PolymorphicTypeValidator} restrito já encontrado em
 * {@code JdbcAuthorizationServerConfig} — {@code String} nunca precisa de resolução
 * polimórfica (é um tipo concreto final), evitando o mesmo problema sem precisar
 * reconfigurar o mapper deste repositório também.
 */
@Service
@RequiredArgsConstructor
public class ClienteLojaService {

    private static final String PREFIXO_CLIENT_ID = "loja-";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RegisteredClientRepository registeredClientRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public ClienteLojaResponse provisionar(ClienteLojaRequest request) {
        String clientId = PREFIXO_CLIENT_ID + request.empresaId();
        if (registeredClientRepository.findByClientId(clientId) != null) {
            throw new ConflictException(SegurancaErrorCode.CLIENTE_LOJA_JA_EXISTE);
        }

        String segredo = gerarSegredo();
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(segredo))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("internal")
                .clientSettings(ClientSettings.builder()
                        .setting("lojaId", String.valueOf(request.empresaId()))
                        .setting("descricao", request.descricao())
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15))
                        .build())
                .build();
        registeredClientRepository.save(client);

        return new ClienteLojaResponse(clientId, segredo, request.empresaId(), request.descricao());
    }

    /** Sem paginação de propósito: volume esperado é "algumas centenas de lojas", mesma ordem de grandeza já assumida em toda a Fase 6. */
    public List<ClienteLojaResumoResponse> listar() {
        List<String> clientIds = jdbcTemplate.queryForList(
                "SELECT client_id FROM oauth2_registered_client WHERE client_id LIKE ? ORDER BY client_id",
                String.class, PREFIXO_CLIENT_ID + "%");

        return clientIds.stream()
                .map(registeredClientRepository::findByClientId)
                .filter(Objects::nonNull)
                .map(client -> new ClienteLojaResumoResponse(
                        client.getClientId(),
                        Long.valueOf(client.getClientSettings().<String>getSetting("lojaId")),
                        client.getClientSettings().<String>getSetting("descricao")))
                .toList();
    }

    private String gerarSegredo() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
