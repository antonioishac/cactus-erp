package br.com.cactusdigital.erp.sincronizacao.messaging;

import br.com.cactusdigital.erp.sincronizacao.config.RabbitConfig;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.repository.RegistroSincronizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @RabbitListener(queues = {RabbitConfig.FILA_ESTOQUE_MOVIMENTO_DLQ, RabbitConfig.FILA_FINANCEIRO_PDV_DLQ})
    @Transactional
    public void receber(String mensagemJson) {
        try {
            JsonNode node = objectMapper.readTree(mensagemJson);
            Long registroId = node.get("registroId").asLong();
            repository.findById(registroId).ifPresentOrElse(
                    registro -> {
                        registro.setStatus(StatusRegistroSincronizacao.ERRO);
                        registro.setMotivoErro("Esgotou tentativas de processamento no consumidor de domínio — ver DLQ do RabbitMQ");
                        registro.setTentativas(registro.getTentativas() + 1);
                        repository.save(registro);
                    },
                    () -> log.warn("Mensagem na DLQ final pra registroId={} que não existe mais", registroId));
        } catch (Exception ex) {
            log.error("Falha ao processar mensagem da DLQ final de sincronização: {}", mensagemJson, ex);
        }
    }
}
