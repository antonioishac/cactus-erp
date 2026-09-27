package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PlanoSituacao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * Cliente HTTP para o endpoint de validação de licença do Cactus ERP
 * (retaguarda) — ver {@code LICENCIAMENTO.md} na raiz do projeto.
 *
 * <p><b>⚠️ Contrato não confirmado.</b> O endpoint da retaguarda ainda não
 * existe (é outro módulo, fora deste repositório). O contrato assumido
 * aqui — {@code GET <url>?dispositivo=<hostname>} com header
 * {@code Authorization: Bearer <token>}, resposta JSON
 * {@code {"situacao": "A"}} — é uma <b>proposta razoável</b>, não uma
 * especificação validada. Ajustar quando o endpoint real for definido.
 *
 * <p>Desabilitado (retorna sempre {@link Optional#empty()}) quando
 * {@code cactus.pdv.licenca.retaguarda-url} não está configurado —
 * comportamento padrão hoje, já que o endpoint não existe. Nesse caso
 * {@code LicencaService} opera 100% em modo local, como documentado.
 *
 * <p>Usa apenas {@code java.net.http.HttpClient} (JDK) — sem dependência
 * de cliente HTTP adicional.
 */
@Component
public class RetaguardaLicencaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetaguardaLicencaClient.class);

    @Value("${cactus.pdv.licenca.retaguarda-url:}")
    private String retaguardaUrl;

    @Value("${cactus.pdv.licenca.token:}")
    private String token;

    @Value("${cactus.pdv.licenca.timeout-ms:5000}")
    private long timeoutMs;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** {@code true} se a URL da retaguarda foi configurada. */
    public boolean estaConfigurado() {
        return retaguardaUrl != null && !retaguardaUrl.isBlank();
    }

    /**
     * Consulta a situação do plano na retaguarda.
     *
     * @param identificadorDispositivo hostname do PDV (vínculo por dispositivo)
     * @return situação retornada, ou {@link Optional#empty()} se desabilitado,
     *         offline, timeout, ou resposta em formato inesperado — o
     *         caller ({@code LicencaService}) decide o fallback.
     */
    public Optional<PlanoSituacao> consultarSituacao(String identificadorDispositivo) {
        if (!estaConfigurado()) {
            return Optional.empty();
        }

        try {
            String urlComQuery = retaguardaUrl
                    + (retaguardaUrl.contains("?") ? "&" : "?")
                    + "dispositivo=" + URLEncoder.encode(
                            identificadorDispositivo != null ? identificadorDispositivo : "",
                            StandardCharsets.UTF_8);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(urlComQuery))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .GET();

            if (token != null && !token.isBlank()) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpResponse<String> resposta = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (resposta.statusCode() != 200) {
                LOGGER.warn("Retaguarda respondeu status {} ao consultar licença.", resposta.statusCode());
                return Optional.empty();
            }

            JsonNode node = objectMapper.readTree(resposta.body());
            String situacao = node.path("situacao").asText(null);
            if (situacao == null || situacao.isBlank()) {
                LOGGER.warn("Resposta da retaguarda sem campo 'situacao': {}", resposta.body());
                return Optional.empty();
            }

            return Optional.of(PlanoSituacao.valueOf(situacao.trim().toUpperCase()));

        } catch (Exception ex) {
            // Qualquer falha (rede, timeout, parsing) degrada pra offline —
            // quem decide o que fazer é o LicencaService (cache + tolerância).
            LOGGER.warn("Falha ao consultar licença na retaguarda: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
