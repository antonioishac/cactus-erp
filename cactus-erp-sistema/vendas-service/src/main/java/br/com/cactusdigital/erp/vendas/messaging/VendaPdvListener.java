package br.com.cactusdigital.erp.vendas.messaging;

import br.com.cactusdigital.erp.vendas.config.RabbitConfig;
import br.com.cactusdigital.erp.vendas.service.VendaPdvService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumidor de {@code vendas.pdv.q}: despacha pelo {@code tipoRegistro} ({@code VENDA_PDV}/{@code DEVOLUCAO_PDV})
 * e publica a confirmação que fecha o registro como {@code PROCESSADO} no sincronizacao-service. Reentrega de
 * um registro já aplicado também é confirmada. Não captura exceção — propagar aciona o retry/DLQ.
 */
@Component
@RequiredArgsConstructor
public class VendaPdvListener {

    private static final Logger log = LoggerFactory.getLogger(VendaPdvListener.class);

    private final VendaPdvService service;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.FILA_VENDAS_PDV, containerFactory = "rabbitListenerContainerFactory")
    public void receber(String mensagemJson) {
        JsonNode mensagem = objectMapper.readTree(mensagemJson);
        Long registroId = mensagem.get("registroId").asLong();
        String tipo = mensagem.get("tipoRegistro").asString();

        boolean aplicado = switch (tipo) {
            case "VENDA_PDV" -> service.registrarVenda(mensagem);
            case "DEVOLUCAO_PDV" -> service.registrarDevolucao(mensagem);
            default -> throw new IllegalArgumentException("tipoRegistro sem destino no vendas-service: " + tipo);
        };

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY_CONFIRMACAO_VENDAS_PDV,
                objectMapper.writeValueAsString(Map.of("registroId", registroId, "status", "PROCESSADO")));
        log.info("Registro do PDV {} — registroId={} ({})", aplicado ? "aplicado no vendas-service" : "já aplicado antes, só reconfirmado",
                registroId, tipo);
    }
}
