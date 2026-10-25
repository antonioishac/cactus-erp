package br.com.cactusdigital.erp.sincronizacao.service;

import br.com.cactusdigital.erp.sincronizacao.config.RabbitConfig;
import br.com.cactusdigital.erp.sincronizacao.entity.RegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.entity.TipoRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.repository.RegistroSincronizacaoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Padrão Outbox (seção 5.3 do FASE6_SINCRONIZACAO_PDV.md): {@link RegistroSincronizacao}
 * já está durável em {@code RECEBIDO} antes de qualquer tentativa de publicar no
 * RabbitMQ (feito por {@code LoteSincronizacaoPushService}, em transação local). Este
 * job varre os pendentes e publica — se o broker estava fora do ar no momento do
 * {@code POST}, o dado já está seguro no MySQL e será publicado assim que o RabbitMQ
 * voltar, sem que o PDV precise saber ou reenviar (cenário 6 da seção 8).
 */
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayService.class);

    private final RegistroSincronizacaoRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${cactus.sincronizacao.outbox-relay-fixed-delay-ms:5000}")
    public void relay() {
        List<RegistroSincronizacao> pendentes = repository.findByStatus(StatusRegistroSincronizacao.RECEBIDO);
        for (RegistroSincronizacao registro : pendentes) {
            publicarUm(registro);
        }
    }

    @Transactional
    public void publicarUm(RegistroSincronizacao registro) {
        try {
            String routingKey = routingKeyPara(registro.getTipoRegistro());
            ObjectNode mensagem = (ObjectNode) objectMapper.readTree(registro.getPayload());
            mensagem.put("registroId", registro.getId());
            if (registro.getTipoRegistro() != TipoRegistroSincronizacao.ESTOQUE_MOVIMENTO) {
                // Financeiro e vendas têm uma fila só cada: o consumidor despacha pelo tipo, e precisa da
                // loja (= empresaId no ERP). idOrigem vai junto porque a devolução acha a venda por ele.
                mensagem.put("tipoRegistro", registro.getTipoRegistro().name());
                mensagem.put("lojaId", registro.getLojaId());
                mensagem.put("idOrigem", registro.getIdOrigem());
            }
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, mensagem.toString());

            registro.setStatus(StatusRegistroSincronizacao.PUBLICADO);
            repository.save(registro);
        } catch (Exception ex) {
            // Broker fora do ar ou payload corrompido — fica em RECEBIDO, próximo ciclo
            // do relay tenta de novo. Não sobe pra ERRO aqui: ERRO é reservado pra falha
            // de PROCESSAMENTO reportada pela DLQ final (ver DlqListener), não de publicação.
            log.warn("Falha ao publicar registro de sincronização {} — tentando de novo no próximo ciclo: {}", registro.getId(), ex.getMessage());
        }
    }

    private String routingKeyPara(TipoRegistroSincronizacao tipo) {
        return switch (tipo) {
            case ESTOQUE_MOVIMENTO -> RabbitConfig.ROUTING_KEY_ESTOQUE_MOVIMENTO;
            case SANGRIA, SUPRIMENTO, ABERTURA_CAIXA, CONFERENCIA_CAIXA, FIADO_LANCAMENTO -> RabbitConfig.ROUTING_KEY_FINANCEIRO_PDV;
            case VENDA_PDV, DEVOLUCAO_PDV -> RabbitConfig.ROUTING_KEY_VENDAS_PDV;
        };
    }
}
