package br.com.cactusdigital.erp.sincronizacao.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia do lado produtor/orquestrador — ver
 * {@code br.com.cactusdigital.erp.estoque.config.RabbitConfig} (controle-estoque-service)
 * pro lado consumidor de {@code estoque.movimento.q} e pro porquê da simplificação de
 * retry/DLQ em relação ao desenho em cascata do FASE6_SINCRONIZACAO_PDV.md.
 * <p>
 * Exchange/DLX são declarados aqui TAMBÉM (idempotente, mesmo nome/tipo/durabilidade do
 * outro lado) — nenhum dos dois serviços depende da ordem de subida do outro pra criar
 * a topologia.
 * <p>
 * Este serviço consome dois fluxos vindos de volta do controle-estoque-service:
 * {@link #FILA_CONFIRMACOES} (sucesso — fecha {@code RegistroSincronizacao} pra
 * {@code PROCESSADO}) e {@link #FILA_ESTOQUE_MOVIMENTO_DLQ} (falha esgotou retry — fecha
 * pra {@code ERRO}). Ver {@code ConfirmacaoListener}/{@code DlqListener}.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "cactus.sincronizacao";
    public static final String DLX = "cactus.sincronizacao.dlx";

    public static final String ROUTING_KEY_ESTOQUE_MOVIMENTO = "estoque.movimento";
    public static final String ROUTING_KEY_ESTOQUE_MOVIMENTO_DLQ = "estoque.movimento.dlq";
    public static final String ROUTING_KEY_CONFIRMACAO_WILDCARD = "confirmacao.#";

    public static final String FILA_ESTOQUE_MOVIMENTO_DLQ = "estoque.movimento.q.dlq";
    public static final String FILA_CONFIRMACOES = "sincronizacao.confirmacoes.q";

    @Bean
    public TopicExchange sincronizacaoExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange sincronizacaoDlx() {
        return new TopicExchange(DLX, true, false);
    }

    /** Mesma declaração exata do controle-estoque-service — RabbitMQ exige que as
     * propriedades de uma fila batam entre serviços diferentes que a declaram. */
    @Bean
    public Queue estoqueMovimentoDlq() {
        return QueueBuilder.durable(FILA_ESTOQUE_MOVIMENTO_DLQ).build();
    }

    @Bean
    public Binding estoqueMovimentoDlqBinding(Queue estoqueMovimentoDlq, TopicExchange sincronizacaoDlx) {
        return BindingBuilder.bind(estoqueMovimentoDlq).to(sincronizacaoDlx).with(ROUTING_KEY_ESTOQUE_MOVIMENTO_DLQ);
    }

    @Bean
    public Queue confirmacoesQueue() {
        return QueueBuilder.durable(FILA_CONFIRMACOES).build();
    }

    @Bean
    public Binding confirmacoesBinding(Queue confirmacoesQueue, TopicExchange sincronizacaoExchange) {
        return BindingBuilder.bind(confirmacoesQueue).to(sincronizacaoExchange).with(ROUTING_KEY_CONFIRMACAO_WILDCARD);
    }
}
