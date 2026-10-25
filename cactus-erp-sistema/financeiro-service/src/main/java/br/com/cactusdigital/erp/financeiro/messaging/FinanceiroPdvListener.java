package br.com.cactusdigital.erp.financeiro.messaging;

import br.com.cactusdigital.erp.financeiro.config.RabbitConfig;
import br.com.cactusdigital.erp.financeiro.service.FinanceiroPdvService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumidor de {@code financeiro.pdv.q} (Fase 6-B): aplica via {@link FinanceiroPdvService} e
 * publica a confirmação que fecha o registro como {@code PROCESSADO} no sincronizacao-service.
 * Reentrega de um registro já aplicado também é confirmada (a confirmação anterior pode ter se
 * perdido), sem lançar nada de novo. Não captura exceção de propósito — propagar é o que aciona o
 * retry e, esgotado, a DLQ (ver {@link RabbitConfig}).
 */
@Component
@RequiredArgsConstructor
public class FinanceiroPdvListener {

    private static final Logger log = LoggerFactory.getLogger(FinanceiroPdvListener.class);

    private final FinanceiroPdvService service;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.FILA_FINANCEIRO_PDV, containerFactory = "rabbitListenerContainerFactory")
    public void receber(String mensagemJson) {
        JsonNode mensagem = objectMapper.readTree(mensagemJson);
        Long registroId = mensagem.get("registroId").asLong();

        boolean aplicado = service.processar(mensagem);

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY_CONFIRMACAO_FINANCEIRO_PDV,
                objectMapper.writeValueAsString(java.util.Map.of("registroId", registroId, "status", "PROCESSADO")));
        log.info("Registro do PDV {} — registroId={} ({})", aplicado ? "aplicado no financeiro" : "já aplicado antes, só reconfirmado",
                registroId, mensagem.get("tipoRegistro").asString());
    }
}
