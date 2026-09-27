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
 * Consome {@code sincronizacao.confirmacoes.q} — publicada pelo consumidor de domínio
 * (hoje só {@code controle-estoque-service}) depois de aplicar um registro com sucesso.
 * Fecha {@link StatusRegistroSincronizacao#PROCESSADO}. Ver seção 6.5 do
 * FASE6_SINCRONIZACAO_PDV.md.
 * <p>
 * Captura exceção internamente de propósito, nunca deixa propagar — evita loop de
 * redelivery por uma mensagem de confirmação malformada (não há DLQ dedicada pra essa
 * fila, o custo de perder uma confirmação isolada é baixo: o registro fica em
 * {@code PUBLICADO} pra sempre, visível via {@code GET /sync/registros}, não some
 * silenciosamente — corrigível manualmente se algum dia acontecer de verdade).
 */
@Component
@RequiredArgsConstructor
public class ConfirmacaoListener {

    private static final Logger log = LoggerFactory.getLogger(ConfirmacaoListener.class);

    private final RegistroSincronizacaoRepository repository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.FILA_CONFIRMACOES)
    @Transactional
    public void receber(String mensagemJson) {
        try {
            JsonNode node = objectMapper.readTree(mensagemJson);
            Long registroId = node.get("registroId").asLong();
            repository.findById(registroId).ifPresentOrElse(
                    registro -> {
                        registro.setStatus(StatusRegistroSincronizacao.PROCESSADO);
                        repository.save(registro);
                    },
                    () -> log.warn("Confirmação recebida pra registroId={} que não existe mais", registroId));
        } catch (Exception ex) {
            log.error("Falha ao processar mensagem de confirmação de sincronização: {}", mensagemJson, ex);
        }
    }
}
