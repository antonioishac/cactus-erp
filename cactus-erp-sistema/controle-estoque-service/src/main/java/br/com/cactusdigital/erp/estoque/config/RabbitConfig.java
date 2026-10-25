package br.com.cactusdigital.erp.estoque.config;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.time.Duration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia do lado consumidor de {@code estoque.movimento.q} — publicada pelo
 * {@code sincronizacao-service} (Fase 6, sincronização ERP↔PDV) quando o PDV empurra
 * {@code ESTOQUE_MOVIMENTO}. Ver {@code EstoqueSincronizacaoListener} pro consumo em si.
 * <p>
 * Exchange/DLX são declarados aqui de propósito (idempotente — mesmo nome/tipo/
 * durabilidade que o {@code sincronizacao-service} declara do lado produtor): cada
 * serviço consumidor é dono da própria fila/binding, não depende de o produtor ter
 * subido primeiro pra criar a topologia (padrão RabbitMQ padrão — declaração é
 * idempotente entre serviços diferentes).
 * <p>
 * <b>Retry/DLQ — simplificação deliberada do desenho em cascata de TTL descrito em
 * {@code FASE6_SINCRONIZACAO_PDV.md} (seção 6.2)</b>: em vez de 3 filas intermediárias
 * com TTL crescente (30s→2min→10min) redirecionando de volta pra fila principal via
 * DLX, uso {@link StatelessRetryOperationsInterceptor} (retry em memória, com backoff, no
 * próprio consumidor — via {@link RetryInterceptorBuilder} do próprio Spring AMQP, não
 * da lib {@code spring-retry}: Spring AMQP 4.1/Spring Framework 7 passou a usar o novo
 * framework de retry embutido, {@code org.springframework.core.retry}) +
 * {@link RepublishMessageRecoverer} (publica na DLQ final só depois de esgotar as
 * tentativas). Resultado observável é o mesmo do documento — backoff entre tentativas,
 * máximo 5 tentativas, DLQ final — com bem menos infraestrutura de filas pra manter; a
 * diferença é que o retry acontece bloqueando a thread do consumidor em vez de via
 * mensagem recirculando pelo broker. Registrado aqui, não escondido — evolui pro desenho
 * em cascata se o volume um dia justificar retry não-bloqueante.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "cactus.sincronizacao";
    public static final String DLX = "cactus.sincronizacao.dlx";
    public static final String FILA_ESTOQUE_MOVIMENTO = "estoque.movimento.q";
    public static final String FILA_ESTOQUE_MOVIMENTO_DLQ = "estoque.movimento.q.dlq";
    public static final String ROUTING_KEY_ESTOQUE_MOVIMENTO = "estoque.movimento";
    public static final String ROUTING_KEY_ESTOQUE_MOVIMENTO_DLQ = "estoque.movimento.dlq";
    public static final String ROUTING_KEY_CONFIRMACAO_ESTOQUE_MOVIMENTO = "confirmacao.estoque.movimento";

    @Bean
    public TopicExchange sincronizacaoExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange sincronizacaoDlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Queue estoqueMovimentoQueue() {
        return QueueBuilder.durable(FILA_ESTOQUE_MOVIMENTO)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_ESTOQUE_MOVIMENTO_DLQ)
                .build();
    }

    @Bean
    public Binding estoqueMovimentoBinding(Queue estoqueMovimentoQueue, TopicExchange sincronizacaoExchange) {
        return BindingBuilder.bind(estoqueMovimentoQueue).to(sincronizacaoExchange).with(ROUTING_KEY_ESTOQUE_MOVIMENTO);
    }

    @Bean
    public Queue estoqueMovimentoDlq() {
        return QueueBuilder.durable(FILA_ESTOQUE_MOVIMENTO_DLQ).build();
    }

    @Bean
    public Binding estoqueMovimentoDlqBinding(Queue estoqueMovimentoDlq, TopicExchange sincronizacaoDlx) {
        return BindingBuilder.bind(estoqueMovimentoDlq).to(sincronizacaoDlx).with(ROUTING_KEY_ESTOQUE_MOVIMENTO_DLQ);
    }

    @Bean
    public StatelessRetryOperationsInterceptor estoqueMovimentoRetryInterceptor(RabbitTemplate rabbitTemplate) {
        RepublishMessageRecoverer recoverer = new RepublishMessageRecoverer(rabbitTemplate, DLX, ROUTING_KEY_ESTOQUE_MOVIMENTO_DLQ);
        return RetryInterceptorBuilder.stateless()
                .configureRetryPolicy(politica -> politica
                        .maxRetries(5)
                        .delay(Duration.ofSeconds(2))
                        .multiplier(3.0)
                        .maxDelay(Duration.ofSeconds(30))
                        // Erro de regra de negócio é determinístico: repetir dá o mesmo resultado,
                        // então vai direto pra DLQ (com o motivo no header x-exception-message).
                        .predicate(erro -> !isErroDeNegocio(erro)))
                .recoverer(recoverer)
                .build();
    }

    /** Procura na cadeia de causas — o container embrulha a exceção do listener em
     * {@code ListenerExecutionFailedException}, então a original nunca é a de fora. */
    static boolean isErroDeNegocio(Throwable erro) {
        for (Throwable atual = erro; atual != null; atual = atual.getCause()) {
            if (atual instanceof BusinessException || atual instanceof ConflictException || atual instanceof NotFoundException) {
                return true;
            }
            if (atual.getCause() == atual) {
                break;
            }
        }
        return false;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory,
            StatelessRetryOperationsInterceptor estoqueMovimentoRetryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAdviceChain(estoqueMovimentoRetryInterceptor);
        return factory;
    }
}
