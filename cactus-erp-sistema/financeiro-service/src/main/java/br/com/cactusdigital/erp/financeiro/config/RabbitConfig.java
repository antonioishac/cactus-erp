package br.com.cactusdigital.erp.financeiro.config;

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
 * Lado consumidor de {@code financeiro.pdv.q} (Fase 6-B) — caixa e fiado do PDV publicados pelo
 * {@code sincronizacao-service}. Mesma topologia e mesmo retry/DLQ do consumidor de estoque
 * ({@code controle-estoque-service.config.RabbitConfig}, ver o Javadoc de lá pra simplificação
 * deliberada do retry em cascata): exchange/DLX declarados nos dois lados (idempotente), 5
 * tentativas com backoff, depois DLQ — que o sincronizacao-service consome pra marcar o
 * registro como {@code ERRO}.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "cactus.sincronizacao";
    public static final String DLX = "cactus.sincronizacao.dlx";
    public static final String FILA_FINANCEIRO_PDV = "financeiro.pdv.q";
    public static final String FILA_FINANCEIRO_PDV_DLQ = "financeiro.pdv.q.dlq";
    public static final String ROUTING_KEY_FINANCEIRO_PDV = "financeiro.pdv";
    public static final String ROUTING_KEY_FINANCEIRO_PDV_DLQ = "financeiro.pdv.dlq";
    public static final String ROUTING_KEY_CONFIRMACAO_FINANCEIRO_PDV = "confirmacao.financeiro.pdv";

    @Bean
    public TopicExchange sincronizacaoExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange sincronizacaoDlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Queue financeiroPdvQueue() {
        return QueueBuilder.durable(FILA_FINANCEIRO_PDV)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_FINANCEIRO_PDV_DLQ)
                .build();
    }

    @Bean
    public Binding financeiroPdvBinding(Queue financeiroPdvQueue, TopicExchange sincronizacaoExchange) {
        return BindingBuilder.bind(financeiroPdvQueue).to(sincronizacaoExchange).with(ROUTING_KEY_FINANCEIRO_PDV);
    }

    /** Mesma declaração exata do sincronizacao-service — RabbitMQ exige que as propriedades batam. */
    @Bean
    public Queue financeiroPdvDlq() {
        return QueueBuilder.durable(FILA_FINANCEIRO_PDV_DLQ).build();
    }

    @Bean
    public Binding financeiroPdvDlqBinding(Queue financeiroPdvDlq, TopicExchange sincronizacaoDlx) {
        return BindingBuilder.bind(financeiroPdvDlq).to(sincronizacaoDlx).with(ROUTING_KEY_FINANCEIRO_PDV_DLQ);
    }

    @Bean
    public StatelessRetryOperationsInterceptor financeiroPdvRetryInterceptor(RabbitTemplate rabbitTemplate) {
        RepublishMessageRecoverer recoverer = new RepublishMessageRecoverer(rabbitTemplate, DLX, ROUTING_KEY_FINANCEIRO_PDV_DLQ);
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
            StatelessRetryOperationsInterceptor financeiroPdvRetryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAdviceChain(financeiroPdvRetryInterceptor);
        // Consumidor ÚNICO de propósito: a ordem dos lançamentos importa (o pagamento de um fiado não pode
        // ser processado antes da compra que ele quita, que vem antes no mesmo lote). Ver o contraponto no
        // RabbitConfig do vendas-service.
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }
}
