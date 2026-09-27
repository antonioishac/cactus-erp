package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.common.saga.SagaOrchestrator;
import br.com.cactusdigital.erp.common.saga.SagaStep;
import br.com.cactusdigital.erp.compras.client.EstoqueClient;
import br.com.cactusdigital.erp.compras.client.EstoqueMovimentoClientRequest;
import br.com.cactusdigital.erp.compras.client.FinanceiroClient;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraItemRequest;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraResponse;
import br.com.cactusdigital.erp.compras.entity.PedidoCompra;
import br.com.cactusdigital.erp.compras.entity.PedidoCompraItem;
import br.com.cactusdigital.erp.compras.entity.RecebimentoCompra;
import br.com.cactusdigital.erp.compras.entity.RecebimentoCompraItem;
import br.com.cactusdigital.erp.compras.entity.SituacaoPedidoCompra;
import br.com.cactusdigital.erp.compras.exception.ComprasErrorCode;
import br.com.cactusdigital.erp.compras.mapper.ComprasMapper;
import br.com.cactusdigital.erp.compras.repository.RecebimentoCompraItemRepository;
import br.com.cactusdigital.erp.compras.repository.RecebimentoCompraRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confirma o recebimento (total ou parcial) de um {@link PedidoCompra}. Cross-serviço
 * orquestrado via {@link SagaOrchestrator} (piloto original em
 * {@code vendas-service.VendaService#confirmar}, ver Javadoc lá): N passos "dar-entrada-
 * estoque-item-X" (uma {@link EstoqueClient#registrarEntrada} por item, na unidade de
 * <b>estoque</b> — quantidade × fatorConversao, custo unitário ÷ fatorConversao, nunca na
 * unidade de compra do pedido) seguidos, quando este recebimento completa o pedido pela
 * primeira vez, de 1 passo "gerar-contas-a-pagar". Se um passo falhar, os já executados
 * são compensados em ordem reversa antes da exceção original ser relançada — ver Javadoc
 * de {@link SagaOrchestrator} pro que isso garante (e pro que continua sendo limitação
 * aceita). Toda execução é auditada em {@code SagaExecucao}/{@code SagaExecucaoPasso}
 * (via {@code RecebimentoSagaAuditor}), consultável em {@code GET /api/v1/sagas}.
 */
@Service
@RequiredArgsConstructor
public class RecebimentoCompraService {

    private final RecebimentoCompraRepository repository;
    private final RecebimentoCompraItemRepository itemRepository;
    private final PedidoCompraService pedidoCompraService;
    private final EstoqueClient estoqueClient;
    private final FinanceiroClient financeiroClient;
    private final SagaOrchestrator sagaOrchestrator;
    private final SagaAuditoriaService sagaAuditoriaService;
    private final ComprasMapper mapper;

    @Transactional(readOnly = true)
    public Page<RecebimentoCompraResponse> listarPorPedido(Long pedidoId, Pageable pageable) {
        Page<RecebimentoCompra> pagina = repository.findByPedidoId(pedidoId, pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, RecebimentoCompra::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public RecebimentoCompraResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public RecebimentoCompraResponse registrar(RecebimentoCompraRequest request, Long usuarioId) {
        PedidoCompra pedido = pedidoCompraService.buscarEntidade(request.pedidoId());
        boolean situacaoValida = pedido.getSituacao() == SituacaoPedidoCompra.ENVIADO
                || pedido.getSituacao() == SituacaoPedidoCompra.PARCIALMENTE_RECEBIDO;
        if (!situacaoValida) {
            throw new ConflictException(ComprasErrorCode.PEDIDO_SITUACAO_INVALIDA);
        }

        RecebimentoCompra recebimento = RecebimentoCompra.builder()
                .pedido(pedido)
                .usuarioId(usuarioId)
                .dataRecebimento(request.dataRecebimento())
                .numeroNotaFiscal(request.numeroNotaFiscal())
                .chaveAcessoNfe(request.chaveAcessoNfe())
                .observacao(request.observacao())
                .build();
        recebimento = repository.save(recebimento);

        // Preparação puramente LOCAL (persiste RecebimentoCompraItem, atualiza
        // PedidoCompraItem.quantidadeRecebida) — ainda SEM nenhuma chamada cross-serviço.
        // A chamada cross-serviço só acontece depois, via saga (ver construirPassos), pra
        // que uma falha no meio tenha o que compensar de forma coerente.
        List<ItemParaEstoque> itensParaEstoque = new ArrayList<>();
        for (RecebimentoCompraItemRequest itemRequest : request.itens()) {
            itensParaEstoque.add(prepararItem(recebimento, itemRequest));
        }

        boolean completaPedido = pedidoCompraService.verificarTransicaoParaRecebidoTotal(pedido.getId());

        sagaOrchestrator.executar("RECEBER_COMPRA", recebimento,
                construirPassos(recebimento, pedido, itensParaEstoque, completaPedido),
                new RecebimentoSagaAuditor(sagaAuditoriaService));

        return mapper.toResponse(recebimento);
    }

    private ItemParaEstoque prepararItem(RecebimentoCompra recebimento, RecebimentoCompraItemRequest itemRequest) {
        PedidoCompraItem pedidoItem = pedidoCompraService.buscarItemEntidade(
                recebimento.getPedido().getId(), itemRequest.pedidoCompraItemId());

        if (itemRequest.quantidadeRecebida().compareTo(pedidoItem.getQuantidadePendente()) > 0) {
            throw new BusinessException(ComprasErrorCode.RECEBIMENTO_QUANTIDADE_EXCEDE_PEDIDO);
        }

        // custo/quantidade convertidos da unidade de COMPRA (a do pedido) pra unidade de
        // ESTOQUE (a que o controle-estoque-service entende) — ver Javadoc da classe.
        BigDecimal quantidadeEstoque = itemRequest.quantidadeRecebida().multiply(pedidoItem.getFatorConversaoEstoque());
        BigDecimal custoUnitarioEstoque = pedidoItem.getValorUnitario()
                .divide(pedidoItem.getFatorConversaoEstoque(), 6, RoundingMode.HALF_UP);

        pedidoItem.setQuantidadeRecebida(pedidoItem.getQuantidadeRecebida().add(itemRequest.quantidadeRecebida()));

        // save explícito, mesmo achado real do vendas-service (Fase 7) — GenerationType.
        // IDENTITY só atribui o id no INSERT de verdade; sem isso, o item apareceria com
        // id=null na resposta deste POST (recebimento já é entidade gerenciada aqui).
        RecebimentoCompraItem item = itemRepository.save(RecebimentoCompraItem.builder()
                .recebimento(recebimento)
                .pedidoCompraItem(pedidoItem)
                .quantidadeRecebida(itemRequest.quantidadeRecebida())
                .depositoId(itemRequest.depositoId())
                .loteId(itemRequest.loteId())
                .build());
        recebimento.getItens().add(item);

        return new ItemParaEstoque(item.getId(), pedidoItem.getProdutoId(), itemRequest.depositoId(),
                itemRequest.loteId(), quantidadeEstoque, custoUnitarioEstoque);
    }

    /**
     * Monta os passos da saga: N "dar-entrada-estoque-item-X" (compensação real via
     * {@link EstoqueClient#registrarSaida}, {@code origemTipo=ESTORNO_COMPRA}) seguidos,
     * só quando {@code completaPedido}, de 1 "gerar-contas-a-pagar" (compensação via
     * {@link FinanceiroClient#estornarContasDeCompra} — não exercitada nesta pilot por ser
     * o ÚLTIMO passo da sequência, mesmo registro de transparência já feito em
     * {@code vendas-service.VendaService#construirPassosConfirmacao}).
     */
    private List<SagaStep<RecebimentoCompra>> construirPassos(RecebimentoCompra recebimento, PedidoCompra pedido,
            List<ItemParaEstoque> itensParaEstoque, boolean completaPedido) {
        List<SagaStep<RecebimentoCompra>> passos = new ArrayList<>();
        for (ItemParaEstoque item : itensParaEstoque) {
            passos.add(new SagaStep<RecebimentoCompra>() {
                @Override
                public String nome() {
                    return "dar-entrada-estoque-item-" + item.recebimentoCompraItemId();
                }

                @Override
                public void executar(RecebimentoCompra r) {
                    estoqueClient.registrarEntrada(new EstoqueMovimentoClientRequest(
                            item.produtoId(), item.depositoId(), item.loteId(), null,
                            "ENTRADA", null, item.quantidadeEstoque(), item.custoUnitarioEstoque(),
                            "COMPRA", r.getId(), null,
                            "Recebimento #" + r.getId() + " — pedido #" + pedido.getId()));
                }

                @Override
                public void compensar(RecebimentoCompra r) {
                    estoqueClient.registrarSaida(new EstoqueMovimentoClientRequest(
                            item.produtoId(), item.depositoId(), item.loteId(), null,
                            "SAIDA", null, item.quantidadeEstoque(), item.custoUnitarioEstoque(),
                            "ESTORNO_COMPRA", r.getId(), null,
                            "Estorno do recebimento #" + r.getId() + " (compensação de saga)"));
                }
            });
        }
        if (completaPedido) {
            passos.add(new SagaStep<RecebimentoCompra>() {
                @Override
                public String nome() {
                    return "gerar-contas-a-pagar";
                }

                @Override
                public void executar(RecebimentoCompra r) {
                    pedidoCompraService.gerarContasPagar(pedido);
                }

                @Override
                public void compensar(RecebimentoCompra r) {
                    financeiroClient.estornarContasDeCompra(pedido.getId());
                }
            });
        }
        return passos;
    }

    /** Valores já convertidos pra unidade de estoque, computados em {@link #prepararItem} — usados pelos passos da saga em {@link #construirPassos}, montados DEPOIS que todo item já foi preparado localmente. */
    private record ItemParaEstoque(
            Long recebimentoCompraItemId,
            Long produtoId,
            Long depositoId,
            Long loteId,
            BigDecimal quantidadeEstoque,
            BigDecimal custoUnitarioEstoque) {
    }

    private RecebimentoCompra buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Recebimento de compra", id));
    }
}
