package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.loja.dto.CancelarPedidoRequest;
import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.entity.ItemPedido;
import br.com.cactusdigital.erp.loja.entity.Pedido;
import br.com.cactusdigital.erp.loja.entity.SituacaoPedido;
import br.com.cactusdigital.erp.loja.exception.LojaVirtualErrorCode;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.PedidoRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pedido — fica atrás do RBAC de sempre (diferente de {@code Carrinho}), ver Javadoc de
 * {@link Pedido}. {@code AGUARDANDO_PAGAMENTO} → {@code PAGO} → {@code EM_SEPARACAO} →
 * {@code ENVIADO} → {@code ENTREGUE}, com {@code CANCELADO} possível a qualquer momento
 * antes de {@code ENVIADO}.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final VarianteProdutoLojaService varianteService;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public Page<PedidoResponse> listar(Long lojaId, Pageable pageable) {
        Page<Pedido> pagina = lojaId != null ? repository.findByLojaId(lojaId, pageable) : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, Pedido::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /**
     * Ação manual/webhook-ready — nesta fatia não existe gateway de pagamento real
     * integrado (ver CLAUDE.md, "Controle financeiro"), então esta é a única forma de
     * marcar um pedido como pago. Quando um gateway real existir, o webhook dele chama
     * este mesmo endpoint (ou um equivalente autenticado por assinatura do provedor).
     * Debita o estoque físico de cada variante e libera a reserva correspondente na mesma
     * chamada (ver {@code VarianteProdutoLojaService#confirmarConsumo}) — só aqui, não na
     * criação do pedido nem no carrinho (ver Javadoc de {@link CarrinhoService}).
     */
    @Transactional
    public PedidoResponse confirmarPagamento(Long id) {
        Pedido pedido = buscarEntidade(id);
        exigirSituacao(pedido, SituacaoPedido.AGUARDANDO_PAGAMENTO);

        for (ItemPedido item : pedido.getItens()) {
            varianteService.confirmarConsumo(item.getVarianteProdutoLojaId(), item.getQuantidade());
        }

        pedido.setSituacao(SituacaoPedido.PAGO);
        pedido.setDataPagamento(Instant.now());
        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse marcarEmSeparacao(Long id) {
        Pedido pedido = buscarEntidade(id);
        exigirSituacao(pedido, SituacaoPedido.PAGO);
        pedido.setSituacao(SituacaoPedido.EM_SEPARACAO);
        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse marcarEnviado(Long id) {
        Pedido pedido = buscarEntidade(id);
        exigirSituacao(pedido, SituacaoPedido.EM_SEPARACAO);
        pedido.setSituacao(SituacaoPedido.ENVIADO);
        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoResponse marcarEntregue(Long id) {
        Pedido pedido = buscarEntidade(id);
        exigirSituacao(pedido, SituacaoPedido.ENVIADO);
        pedido.setSituacao(SituacaoPedido.ENTREGUE);
        return mapper.toResponse(pedido);
    }

    /**
     * Só antes de {@code ENVIADO} — depois disso a mercadoria já saiu, cancelamento
     * exigiria fluxo de devolução (fora de escopo). Devolve o estoque de cada item
     * conforme a situação anterior: se ainda {@code AGUARDANDO_PAGAMENTO}, o estoque físico
     * nunca tinha sido debitado, então só libera a reserva
     * ({@code VarianteProdutoLojaService#liberarReserva}); se já {@code PAGO}/
     * {@code EM_SEPARACAO}, o estoque físico já foi debitado em {@link #confirmarPagamento}
     * e precisa voltar de verdade ({@code VarianteProdutoLojaService#devolverEstoque}).
     */
    @Transactional
    public PedidoResponse cancelar(Long id, CancelarPedidoRequest request) {
        Pedido pedido = buscarEntidade(id);
        SituacaoPedido situacaoAnterior = pedido.getSituacao();
        if (situacaoAnterior == SituacaoPedido.ENVIADO || situacaoAnterior == SituacaoPedido.ENTREGUE
                || situacaoAnterior == SituacaoPedido.CANCELADO) {
            throw new ConflictException(LojaVirtualErrorCode.PEDIDO_SITUACAO_INVALIDA);
        }

        for (ItemPedido item : pedido.getItens()) {
            if (situacaoAnterior == SituacaoPedido.AGUARDANDO_PAGAMENTO) {
                varianteService.liberarReserva(item.getVarianteProdutoLojaId(), item.getQuantidade());
            } else {
                varianteService.devolverEstoque(item.getVarianteProdutoLojaId(), item.getQuantidade());
            }
        }

        pedido.setSituacao(SituacaoPedido.CANCELADO);
        pedido.setMotivoCancelamento(request.motivo());
        return mapper.toResponse(pedido);
    }

    private void exigirSituacao(Pedido pedido, SituacaoPedido esperada) {
        if (pedido.getSituacao() != esperada) {
            throw new ConflictException(LojaVirtualErrorCode.PEDIDO_SITUACAO_INVALIDA);
        }
    }

    Pedido buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Pedido", id));
    }
}
