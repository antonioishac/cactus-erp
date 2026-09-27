package br.com.cactusdigital.erp.estoque.messaging;

import br.com.cactusdigital.erp.estoque.config.RabbitConfig;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoRequest;
import br.com.cactusdigital.erp.estoque.entity.DirecaoAjuste;
import br.com.cactusdigital.erp.estoque.entity.OrigemMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.service.EstoqueMovimentoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumidor de {@code estoque.movimento.q} (Fase 6, sincronização ERP↔PDV) — traduz a
 * mensagem publicada pelo {@code sincronizacao-service} pro
 * {@link EstoqueMovimentoRequest} canônico deste serviço e delega pro mesmo
 * {@link EstoqueMovimentoService} usado pela API REST (nenhuma regra de negócio
 * duplicada: ledger, custeio, aprovação acima de limiar — tudo já se aplica igual).
 * <p>
 * Retry/DLQ ficam inteiramente no {@code adviceChain} do container (ver
 * {@link RabbitConfig#rabbitListenerContainerFactory}) — este método não captura
 * exceção nenhuma de propósito: deixar propagar é o que aciona o retry/republish.
 */
@Component
@RequiredArgsConstructor
public class EstoqueSincronizacaoListener {

    private static final Logger log = LoggerFactory.getLogger(EstoqueSincronizacaoListener.class);

    private final EstoqueMovimentoService estoqueMovimentoService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.FILA_ESTOQUE_MOVIMENTO, containerFactory = "rabbitListenerContainerFactory")
    public void receber(String mensagemJson) {
        EstoqueMovimentoMensagem mensagem = objectMapper.readValue(mensagemJson, EstoqueMovimentoMensagem.class);

        EstoqueMovimentoRequest request = new EstoqueMovimentoRequest(
                mensagem.produtoId(),
                mensagem.depositoId(),
                mensagem.loteId(),
                mensagem.numeroSerieId(),
                TipoMovimentoEstoque.valueOf(mensagem.tipoMovimento()),
                mensagem.direcaoAjuste() != null ? DirecaoAjuste.valueOf(mensagem.direcaoAjuste()) : null,
                mensagem.quantidade(),
                mensagem.custoUnitario(),
                OrigemMovimentoEstoque.valueOf(mensagem.origemTipo()),
                mensagem.origemId(),
                null,
                mensagem.observacao());

        // usuarioId nulo: movimento originado do PDV via serviço-a-serviço, sem JWT
        // humano por trás — ver Javadoc de EstoqueMovimentoMensagem.
        estoqueMovimentoService.registrar(request, null);

        publicarConfirmacao(mensagem.registroId());
        log.info("Movimento de estoque sincronizado do PDV — registroId={}, produtoId={}", mensagem.registroId(), mensagem.produtoId());
    }

    private void publicarConfirmacao(Long registroId) {
        ConfirmacaoSincronizacaoMensagem confirmacao = new ConfirmacaoSincronizacaoMensagem(registroId, "PROCESSADO");
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY_CONFIRMACAO_ESTOQUE_MOVIMENTO, objectMapper.writeValueAsString(confirmacao));
    }
}
