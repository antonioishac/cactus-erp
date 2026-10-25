package br.com.cactusdigital.erp.sincronizacao.messaging;

import br.com.cactusdigital.erp.sincronizacao.config.RabbitConfig;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.repository.RegistroSincronizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Consome a DLQ final de {@code estoque.movimento.q} — mensagem chegou aqui porque o
 * consumidor de domínio esgotou as tentativas (ver Javadoc de
 * {@code br.com.cactusdigital.erp.estoque.config.RabbitConfig} pro desenho de
 * retry/republish). Fecha {@link StatusRegistroSincronizacao#ERRO} — nunca falha
 * silenciosa (seção 8, cenário 8): o registro fica auditável via
 * {@code GET /sync/registros?status=ERRO}, correção é reprocessamento manual
 * ({@code ReprocessamentoService}).
 */
@Component
@RequiredArgsConstructor
public class DlqListener {

    private static final Logger log = LoggerFactory.getLogger(DlqListener.class);

    private final RegistroSincronizacaoRepository repository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {RabbitConfig.FILA_ESTOQUE_MOVIMENTO_DLQ, RabbitConfig.FILA_FINANCEIRO_PDV_DLQ, RabbitConfig.FILA_VENDAS_PDV_DLQ})
    @Transactional
    public void receber(Message mensagem) {
        String mensagemJson = new String(mensagem.getBody(), StandardCharsets.UTF_8);
        String motivo = motivoErro(mensagem);
        try {
            JsonNode node = objectMapper.readTree(mensagemJson);
            Long registroId = node.get("registroId").asLong();
            repository.findById(registroId).ifPresentOrElse(
                    registro -> {
                        registro.setStatus(StatusRegistroSincronizacao.ERRO);
                        registro.setMotivoErro(motivo);
                        registro.setTentativas(registro.getTentativas() + 1);
                        repository.save(registro);
                    },
                    () -> log.warn("Mensagem na DLQ final pra registroId={} que não existe mais", registroId));
        } catch (Exception ex) {
            log.error("Falha ao processar mensagem da DLQ final de sincronização: {}", mensagemJson, ex);
        }
    }

    /**
     * Motivo real da falha, a partir do header {@code x-exception-message} que o
     * {@code RepublishMessageRecoverer} grava ao mandar pra DLQ. Antes disto o motivo era sempre o
     * texto genérico abaixo, e o motivo de verdade (ex: pagamento de fiado maior que a dívida) só
     * existia no log do serviço consumidor.
     */
    private String motivoErro(Message mensagem) {
        Object header = mensagem.getMessageProperties().getHeaders().get("x-exception-message");
        String motivo = header != null && !header.toString().isBlank()
                ? "Falha no consumidor de domínio: " + header
                : "Esgotou tentativas de processamento no consumidor de domínio — ver DLQ do RabbitMQ";
        return motivo.length() <= 500 ? motivo : motivo.substring(0, 500);
    }
}
