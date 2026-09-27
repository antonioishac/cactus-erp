package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.service.dto.ResultadoTef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Cliente HTTP pro cliente TEF local (PayGo/SiTef "Hiperlink") — decisão
 * técnica travada no {@code CLAUDE.md}: TEF Hiperlink é prioridade 1 por
 * ser o padrão atual do mercado (HTTP, sem linha discada), TEF Dial fica
 * como legado via {@code PDV_TIPO_PAGAMENTO.TEF_TIPO_GP}.
 *
 * <p><b>⚠️ CONTRATO NÃO VALIDADO — a peça de menor confiança desta fase.</b>
 * Diferente de ESC/POS (padrão universal) ou do código de barras EAN-13 de
 * balança (padrão nacional documentado), "TEF Hiperlink" não é um
 * protocolo único: PayGo Web, CliSiTef, e as integrações próprias de
 * Stone/Cielo/Rede/GetNet/PagSeguro têm APIs HTTP diferentes entre si. O
 * contrato assumido aqui — {@code POST <url>} com JSON
 * {@code {"valor": 12.50, "tipo": "CREDITO"}}, resposta
 * {@code {"aprovada": true, "nsu": "...", "rede": "...", "cartaoDc": "C"}}
 * — é só uma <b>proposta de forma</b> pra manter a arquitetura pronta.
 *
 * <p><b>⚠️ Achado de pesquisa (2026-08-23) que muda a premissa acima:</b>
 * o "kit de integração" OFICIAL tanto do SiTef quanto do Pay&amp;Go Web é
 * uma <b>DLL nativa Windows</b> (CliSiTef.dll — funções
 * {@code ConfiguraIntSiTefInterativo}/{@code IniciaFuncaoSiTefInterativo}/
 * {@code ContinuaFuncaoSiTefInterativo}/{@code FinalizaFuncaoSiTefInterativo};
 * Pay&amp;Go Web tem DLL equivalente própria), não uma API HTTP/REST local
 * como este cliente assume. O próprio {@code ACBrTEFD} (componente do
 * projeto ACBr, irmão do {@link AcbrMonitorClient} usado no fiscal) integra
 * com SiTef chamando essa DLL diretamente — não existe um "ACBrMonitor pra
 * TEF" equivalente ao que já usamos pro fiscal. Isso significa que, pra
 * qualquer um dos dois líderes de mercado, a integração real provavelmente
 * exigiria <b>JNA</b> (Java Native Access, chamada de DLL nativa a partir do
 * Java) em vez de {@link HttpClient} — mudança de arquitetura, não só de
 * payload. Existe uma variante de terceiro (NTK/ControlPay, ver
 * {@code github.com/autogeral/PayGoWeb}) que parece expor algo mais próximo
 * de HTTP, mas é uma revenda específica, não o Pay&amp;Go oficial —
 * confiança baixa, não investigado a fundo. <b>Documentação real de
 * qualquer uma das opções fica atrás de cadastro/homologação comercial</b>
 * (Software Express pro SiTef, Setis/{@code devpaygo@setis.com.br} pro
 * PayGo) — mesma dinâmica de "preciso de um passo comercial do usuário
 * antes de validar o código de verdade" já vista no certificado ICP-Brasil
 * da fase 6. Ver {@code ROADMAP.md} fase 7 pro estado dessa decisão.
 *
 * <p>Não é chamado por nenhum controller ainda — arquitetura pronta (mas
 * possivelmente pro protocolo errado, ver ressalva acima), falta decidir a
 * solução real e confirmar o mecanismo de integração antes de plugar na
 * tela de pagamento.
 */
@Service
public class TefHiperlinkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TefHiperlinkClient.class);

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TefHiperlinkClient(PdvConfiguracaoRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    /**
     * Processa uma transação de cartão via TEF Hiperlink.
     *
     * @param caixa caixa atual (pra localizar a URL configurada)
     * @param valor valor da transação
     * @param tipo  "CREDITO" ou "DEBITO" — ver ressalva de contrato na classe
     * @return resultado (aprovada/negada) — nunca lança exceção; falha de
     *         comunicação vira {@link ResultadoTef#falha}
     */
    public ResultadoTef processarPagamento(PdvCaixa caixa, BigDecimal valor, String tipo) {
        if (caixa == null || valor == null || valor.signum() <= 0) {
            return ResultadoTef.falha("Parâmetros inválidos para transação TEF.");
        }

        PdvConfiguracao config = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null || config.getTefHiperlinkUrl() == null || config.getTefHiperlinkUrl().isBlank()) {
            return ResultadoTef.falha("TEF Hiperlink não configurado para este caixa.");
        }

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("valor", valor);
            payload.put("tipo", tipo != null ? tipo : "CREDITO");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getTefHiperlinkUrl()))
                    .timeout(Duration.ofSeconds(60)) // transação de cartão pode demorar (senha, aproximação, etc)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resposta = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                LOGGER.error("Cliente TEF respondeu status {} : {}", resposta.statusCode(), resposta.body());
                return ResultadoTef.falha("TEF respondeu status " + resposta.statusCode());
            }

            JsonNode node = objectMapper.readTree(resposta.body());
            boolean aprovada = node.path("aprovada").asBoolean(false);
            String nsu = node.path("nsu").asText(null);
            String rede = node.path("rede").asText(null);
            String cartaoDc = node.path("cartaoDc").asText(null);
            String mensagem = node.path("mensagem").asText(aprovada ? "Transação aprovada." : "Transação negada.");

            return new ResultadoTef(aprovada, nsu, rede, cartaoDc, valor, mensagem);

        } catch (Exception ex) {
            LOGGER.error("Falha na comunicação com o cliente TEF: {}", ex.getMessage(), ex);
            return ResultadoTef.falha("Falha na comunicação com o TEF: " + ex.getMessage());
        }
    }
}
