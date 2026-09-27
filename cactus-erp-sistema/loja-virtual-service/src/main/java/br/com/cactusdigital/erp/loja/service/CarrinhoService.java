package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.loja.dto.AdicionarItemCarrinhoRequest;
import br.com.cactusdigital.erp.loja.dto.AtualizarItemCarrinhoRequest;
import br.com.cactusdigital.erp.loja.dto.CarrinhoResponse;
import br.com.cactusdigital.erp.loja.dto.CriarCarrinhoRequest;
import br.com.cactusdigital.erp.loja.dto.FinalizarCompraRequest;
import br.com.cactusdigital.erp.loja.dto.ItemCarrinhoResponse;
import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.entity.Carrinho;
import br.com.cactusdigital.erp.loja.entity.ClienteLoja;
import br.com.cactusdigital.erp.loja.entity.ItemCarrinho;
import br.com.cactusdigital.erp.loja.entity.ItemPedido;
import br.com.cactusdigital.erp.loja.entity.Pedido;
import br.com.cactusdigital.erp.loja.entity.SituacaoCarrinho;
import br.com.cactusdigital.erp.loja.entity.VarianteProdutoLoja;
import br.com.cactusdigital.erp.loja.exception.LojaVirtualErrorCode;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.CarrinhoRepository;
import br.com.cactusdigital.erp.loja.repository.ItemCarrinhoRepository;
import br.com.cactusdigital.erp.loja.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carrinho — SEM autenticação OAuth2, ver Javadoc de {@link Carrinho} e CLAUDE.md
 * ("Fase 13, sub-fase 2 — decisão de segurança"). {@code token} (UUID) é a própria
 * credencial de posse.
 * <p>
 * <b>Reserva real (Fase 13, sub-fase 4)</b>: adicionar/atualizar/remover item reserva ou
 * libera {@code VarianteProdutoLoja.estoqueReservado} de verdade (lock pessimista, ver
 * {@code VarianteProdutoLojaRepository#buscarParaAtualizarEstoque}) — substitui o
 * "soft check" da sub-fase 2, que permitia dois carrinhos concorrentes passarem juntos na
 * checagem pro mesmo estoque. O estoque físico ({@code estoqueQuantidade}) só é debitado
 * na confirmação de pagamento (ver {@code PedidoService#confirmarPagamento}); até lá, a
 * quantidade fica só reservada, indisponível pra outro carrinho mas ainda no saldo físico.
 * Carrinho abandonado (nunca finalizado) precisa liberar a reserva em algum momento — ver
 * {@link CarrinhoExpiracaoService}.
 */
@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository repository;
    private final ItemCarrinhoRepository itemRepository;
    private final PedidoRepository pedidoRepository;
    private final VarianteProdutoLojaService varianteService;
    private final ClienteLojaService clienteLojaService;
    private final LojaVirtualMapper mapper;

    @Transactional
    public CarrinhoResponse criar(CriarCarrinhoRequest request) {
        Carrinho carrinho = Carrinho.builder()
                .lojaId(request.lojaId())
                .token(UUID.randomUUID().toString())
                .build();
        return mapper.toResponse(repository.save(carrinho));
    }

    @Transactional(readOnly = true)
    public CarrinhoResponse buscarPorToken(String token) {
        return mapper.toResponse(buscarEntidade(token));
    }

    @Transactional
    public ItemCarrinhoResponse adicionarItem(String token, AdicionarItemCarrinhoRequest request) {
        Carrinho carrinho = buscarEntidade(token);
        exigirAberto(carrinho);

        VarianteProdutoLoja variante = varianteService.reservar(request.varianteProdutoLojaId(), request.quantidade());

        ItemCarrinho item = itemRepository.save(ItemCarrinho.builder()
                .carrinho(carrinho)
                .varianteProdutoLoja(variante)
                .quantidade(request.quantidade())
                .precoUnitario(variante.getPrecoPromocional() != null ? variante.getPrecoPromocional() : variante.getPrecoVenda())
                .build());
        carrinho.getItens().add(item);
        return mapper.toResponse(item);
    }

    @Transactional
    public ItemCarrinhoResponse atualizarItem(String token, Long itemId, AtualizarItemCarrinhoRequest request) {
        Carrinho carrinho = buscarEntidade(token);
        exigirAberto(carrinho);
        ItemCarrinho item = buscarItem(carrinho, itemId);
        BigDecimal delta = request.quantidade().subtract(item.getQuantidade());
        varianteService.ajustarReserva(item.getVarianteProdutoLoja().getId(), delta);
        item.setQuantidade(request.quantidade());
        return mapper.toResponse(item);
    }

    @Transactional
    public void removerItem(String token, Long itemId) {
        Carrinho carrinho = buscarEntidade(token);
        exigirAberto(carrinho);
        ItemCarrinho item = buscarItem(carrinho, itemId);
        varianteService.liberarReserva(item.getVarianteProdutoLoja().getId(), item.getQuantidade());
        carrinho.getItens().remove(item);
        itemRepository.delete(item);
    }

    /**
     * Abandono explícito (ex: comprador desiste, frontend chama antes de fechar a aba) —
     * libera a reserva de todos os itens e marca {@code ABANDONADO}. Complementa
     * {@link CarrinhoExpiracaoService}, que cobre o caso sem sinal explícito (aba fechada
     * sem aviso).
     */
    @Transactional
    public void abandonar(String token) {
        Carrinho carrinho = buscarEntidade(token);
        exigirAberto(carrinho);
        for (ItemCarrinho item : carrinho.getItens()) {
            varianteService.liberarReserva(item.getVarianteProdutoLoja().getId(), item.getQuantidade());
        }
        carrinho.setSituacao(SituacaoCarrinho.ABANDONADO);
    }

    /**
     * Checkout — converte o carrinho num {@link Pedido} (snapshot de sku/nome/preço de
     * cada item, ver Javadoc de {@link ItemPedido}), captura/atualiza o
     * {@link ClienteLoja} via guest checkout, marca o carrinho {@code FINALIZADO}.
     * {@code Pedido} nasce {@code AGUARDANDO_PAGAMENTO} — nenhum gateway é chamado
     * aqui, ver Javadoc de {@code PedidoService#confirmarPagamento}.
     */
    @Transactional
    public PedidoResponse finalizarCompra(String token, FinalizarCompraRequest request) {
        Carrinho carrinho = buscarEntidade(token);
        exigirAberto(carrinho);
        if (carrinho.getItens().isEmpty()) {
            throw new BusinessException(LojaVirtualErrorCode.CARRINHO_VAZIO);
        }

        ClienteLoja cliente = clienteLojaService.buscarOuCriar(carrinho.getLojaId(), request);

        Pedido pedido = Pedido.builder()
                .lojaId(carrinho.getLojaId())
                .clienteLoja(cliente)
                .carrinhoId(carrinho.getId())
                .logradouroEntrega(request.logradouroEntrega())
                .numeroEntrega(request.numeroEntrega())
                .complementoEntrega(request.complementoEntrega())
                .bairroEntrega(request.bairroEntrega())
                .cepEntrega(request.cepEntrega())
                .municipioEntrega(request.municipioEntrega())
                .ufEntrega(request.ufEntrega())
                .formaPagamento(request.formaPagamento())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            BigDecimal valorTotalItem = itemCarrinho.getPrecoUnitario().multiply(itemCarrinho.getQuantidade());
            pedido.getItens().add(ItemPedido.builder()
                    .pedido(pedido)
                    .varianteProdutoLojaId(itemCarrinho.getVarianteProdutoLoja().getId())
                    .skuSnapshot(itemCarrinho.getVarianteProdutoLoja().getSku())
                    .nomeSnapshot(itemCarrinho.getVarianteProdutoLoja().getProdutoLoja().getNome())
                    .ncmSnapshot(itemCarrinho.getVarianteProdutoLoja().getNcm())
                    .csosnSnapshot(itemCarrinho.getVarianteProdutoLoja().getCsosn())
                    .quantidade(itemCarrinho.getQuantidade())
                    .precoUnitario(itemCarrinho.getPrecoUnitario())
                    .valorTotal(valorTotalItem)
                    .build());
            subtotal = subtotal.add(valorTotalItem);
        }
        pedido.setValorSubtotal(subtotal);
        pedido.setValorTotal(subtotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        carrinho.setSituacao(SituacaoCarrinho.FINALIZADO);
        return mapper.toResponse(pedidoSalvo);
    }

    private void exigirAberto(Carrinho carrinho) {
        if (carrinho.getSituacao() != SituacaoCarrinho.ABERTO) {
            throw new ConflictException(LojaVirtualErrorCode.CARRINHO_JA_FINALIZADO);
        }
    }

    private ItemCarrinho buscarItem(Carrinho carrinho, Long itemId) {
        return carrinho.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> NotFoundException.of("Item do carrinho", itemId));
    }

    private Carrinho buscarEntidade(String token) {
        return repository.findByToken(token).orElseThrow(() -> NotFoundException.of("Carrinho", token));
    }
}
