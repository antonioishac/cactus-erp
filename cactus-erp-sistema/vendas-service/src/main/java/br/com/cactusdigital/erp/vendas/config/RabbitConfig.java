package br.com.cactusdigital.erp.vendas.config;

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
 * Lado consumidor de {@code vendas.pdv.q} (Fase 6-B, itens 5-7) — vendas e devoluções do PDV publicadas
 * pelo {@code sincronizacao-service}. Mesma topologia e mesmo retry/DLQ dos consumidores de estoque e
 * financeiro: 5 tentativas com backoff pra erro técnico, erro de negócio direto pra DLQ.
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "cactus.sincronizacao";
    public static final String DLX = "cactus.sincronizacao.dlx";
    public static final String FILA_VENDAS_PDV = "vendas.pdv.q";
    public static final String FILA_VENDAS_PDV_DLQ = "vendas.pdv.q.dlq";
    public static final String ROUTING_KEY_VENDAS_PDV = "vendas.pdv";
    public static final String ROUTING_KEY_VENDAS_PDV_DLQ = "vendas.pdv.dlq";
    public static final String ROUTING_KEY_CONFIRMACAO_VENDAS_PDV = "confirmacao.vendas.pdv";

    @Bean
    public TopicExchange sincronizacaoExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange sincronizacaoDlx() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Queue vendasPdvQueue() {
        return QueueBuilder.durable(FILA_VENDAS_PDV)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_VENDAS_PDV_DLQ)
                .build();
    }

    @Bean
    public Binding vendasPdvBinding(Queue vendasPdvQueue, TopicExchange sincronizacaoExchange) {
        return BindingBuilder.bind(vendasPdvQueue).to(sincronizacaoExchange).with(ROUTING_KEY_VENDAS_PDV);
    }

    /** Mesma declaração exata do sincronizacao-service — RabbitMQ exige que as propriedades batam. */
    @Bean
    public Queue vendasPdvDlq() {
        return QueueBuilder.durable(FILA_VENDAS_PDV_DLQ).build();
    }

    @Bean
    public Binding vendasPdvDlqBinding(Queue vendasPdvDlq, TopicExchange sincronizacaoDlx) {
        return BindingBuilder.bind(vendasPdvDlq).to(sincronizacaoDlx).with(ROUTING_KEY_VENDAS_PDV_DLQ);
    }

    @Bean
    public StatelessRetryOperationsInterceptor vendasPdvRetryInterceptor(RabbitTemplate rabbitTemplate) {
        RepublishMessageRecoverer recoverer = new RepublishMessageRecoverer(rabbitTemplate, DLX, ROUTING_KEY_VENDAS_PDV_DLQ);
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
            StatelessRetryOperationsInterceptor vendasPdvRetryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAdviceChain(vendasPdvRetryInterceptor);
        // Mais de um consumidor DE PROPÓSITO (diferente do financeiro): o retry é em memória e segura a
        // thread, então com consumidor único uma devolução que chegou antes da sua venda (lotes fora de
        // ordem) bloqueava a própria venda atrás dela na fila até esgotar as tentativas — achado no teste
        // ao vivo. Com 3, a venda é processada em paralelo e o retry da devolução a encontra. No financeiro
        // a ordem importa (pagamento de fiado não pode passar na frente da compra), lá continua 1 só.
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }
}
