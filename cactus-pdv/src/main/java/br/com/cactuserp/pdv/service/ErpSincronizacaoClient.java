package br.com.cactuserp.pdv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cliente HTTP da sincronização com o ERP ({@code sincronizacao-service}).
 *
 * <p>Autentica como a <b>loja</b> (um client OAuth2 Client Credentials por
 * loja, provisionado no ERP em {@code POST /api/v1/sincronizacao/clientes-loja}
 * — ver FASE6_SINCRONIZACAO_PDV.md, seção 9). O token traz a claim
 * {@code lojaId}, e o ERP recusa (403) chamada para outra loja. O token é
 * reaproveitado até 30s antes de expirar.
 *
 * <p>Sem dependência nova: {@link HttpClient} do JDK + jackson, mesmo padrão de
 * {@code RetaguardaLicencaClient}. O segredo do client vem do ambiente
 * ({@code CACTUS_SYNC_CLIENT_SECRET}), nunca do arquivo versionado.
 */
@Service
public class ErpSincronizacaoClient {

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String erpUrl;
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;

    private String tokenAtual;
    private Instant expiraEm = Instant.EPOCH;

    public ErpSincronizacaoClient(@Value("${cactus.pdv.sync.erp-url:}") String erpUrl,
                                  @Value("${cactus.pdv.sync.token-url:}") String tokenUrl,
                                  @Value("${cactus.pdv.sync.client-id:}") String clientId,
                                  @Value("${cactus.pdv.sync.client-secret:}") String clientSecret) {
        this.erpUrl = erpUrl;
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /** {@code true} quando URL do ERP, URL do token e credenciais estão configuradas. */
    public boolean configurado() {
        return !erpUrl.isBlank() && !tokenUrl.isBlank() && !clientId.isBlank() && !clientSecret.isBlank();
    }

    /** GET em {@code caminho} (ex: {@code /api/v1/sync/produtos}) com os parâmetros dados (nulos são omitidos). */
    public JsonNode get(String caminho, Map<String, Object> parametros) {
        String query = parametros.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + "=" + URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(erpUrl + caminho + (query.isEmpty() ? "" : "?" + query)))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + token())
                .header("Accept", "application/json")
                .GET()
                .build();
        return enviar(request, "GET " + caminho);
    }

    /** POST de um corpo JSON — usado pelo envio (push) da fatia 2. */
    public JsonNode post(String caminho, Object corpo) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(erpUrl + caminho))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + token())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(corpo)))
                    .build();
            return enviar(request, "POST " + caminho);
        } catch (IOException ex) {
            throw new IllegalStateException("Falha ao serializar o envio para o ERP: " + ex.getMessage(), ex);
        }
    }

    private JsonNode enviar(HttpRequest request, String descricao) {
        try {
            HttpResponse<String> resposta = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resposta.statusCode() == 401) {
                tokenAtual = null; // token revogado/expirado antes do previsto: força renovar na próxima
            }
            if (resposta.statusCode() / 100 != 2) {
                throw new IllegalStateException("ERP respondeu " + resposta.statusCode() + " em " + descricao + ": "
                        + truncar(resposta.body(), 300));
            }
            return objectMapper.readTree(resposta.body());
        } catch (IOException ex) {
            throw new IllegalStateException("Falha de comunicação com o ERP em " + descricao + ": " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Comunicação com o ERP interrompida", ex);
        }
    }

    private synchronized String token() {
        if (tokenAtual != null && Instant.now().isBefore(expiraEm.minusSeconds(30))) {
            return tokenAtual;
        }
        Map<String, String> form = new LinkedHashMap<>();
        form.put("grant_type", "client_credentials");
        form.put("scope", "internal");
        String corpo = form.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        String basic = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        HttpRequest request = HttpRequest.newBuilder(URI.create(tokenUrl))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();
        JsonNode resposta = enviarSemToken(request);
        tokenAtual = resposta.path("access_token").asText(null);
        if (tokenAtual == null) {
            throw new IllegalStateException("Servidor de autenticação não devolveu access_token.");
        }
        expiraEm = Instant.now().plusSeconds(resposta.path("expires_in").asLong(60));
        return tokenAtual;
    }

    private JsonNode enviarSemToken(HttpRequest request) {
        try {
            HttpResponse<String> resposta = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resposta.statusCode() / 100 != 2) {
                throw new IllegalStateException("Autenticação no ERP falhou (" + resposta.statusCode() + "): "
                        + truncar(resposta.body(), 300));
            }
            return objectMapper.readTree(resposta.body());
        } catch (IOException ex) {
            throw new IllegalStateException("Falha de comunicação com o servidor de autenticação: " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Autenticação interrompida", ex);
        }
    }

    private static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
