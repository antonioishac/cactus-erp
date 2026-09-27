package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoItemRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoResponse;
import br.com.cactusdigital.erp.vendas.entity.SituacaoOrcamento;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesPagamento;
import br.com.cactusdigital.erp.vendas.entity.VendaOrcamento;
import br.com.cactusdigital.erp.vendas.entity.VendaOrcamentoItem;
import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import br.com.cactusdigital.erp.vendas.exception.VendasErrorCode;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.VendaOrcamentoItemRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaOrcamentoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Máquina de estados do orçamento: {@code RASCUNHO} → (enviar) → {@code ENVIADO} →
 * (aprovar) → {@code APROVADO} → (converter, feito por {@link VendaService}) →
 * {@code CONVERTIDO}. {@code CANCELADO} alcançável de qualquer situação anterior a
 * {@code CONVERTIDO}; {@code EXPIRADO} alcançável de {@code ENVIADO}/{@code APROVADO}
 * (ver {@link SituacaoOrcamento} — melhoria deliberada sobre o fenix, que não tem status
 * explícito de orçamento).
 */
@Service
@RequiredArgsConstructor
public class VendaOrcamentoService {

    private final VendaOrcamentoRepository repository;
    private final VendaOrcamentoItemRepository itemRepository;
    private final VendaCondicoesPagamentoService condicoesPagamentoService;
    private final VendedorService vendedorService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<VendaOrcamentoResponse> listar(Long clienteId, Pageable pageable) {
        Page<VendaOrcamento> pagina = clienteId != null
                ? repository.findByClienteId(clienteId, pageable)
                : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, VendaOrcamento::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VendaOrcamentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public VendaOrcamentoResponse criar(VendaOrcamentoRequest request) {
        VendaCondicoesPagamento condicoes = condicoesPagamentoService.buscarEntidade(request.condicoesPagamentoId());
        Vendedor vendedor = vendedorService.buscarEntidade(request.vendedorId());

        VendaOrcamento orcamento = VendaOrcamento.builder()
                .condicoesPagamento(condicoes)
                .vendedor(vendedor)
                .clienteId(request.clienteId())
                .transportadoraId(request.transportadoraId())
                .codigo(request.codigo())
                .dataCadastro(LocalDate.now())
                .dataEntrega(request.dataEntrega())
                .validade(request.validade())
                .tipoFrete(request.tipoFrete())
                .valorFrete(request.valorFrete())
                .taxaComissao(request.taxaComissao())
                .taxaDesconto(request.taxaDesconto())
                .observacao(request.observacao())
                .situacao(SituacaoOrcamento.RASCUNHO)
                .build();
        return mapper.toResponse(repository.save(orcamento));
    }

    @Transactional
    public VendaOrcamentoItemResponse adicionarItem(Long orcamentoId, VendaOrcamentoItemRequest request) {
        VendaOrcamento orcamento = buscarEntidade(orcamentoId);
        exigirSituacao(orcamento, SituacaoOrcamento.RASCUNHO, VendasErrorCode.ORCAMENTO_ITEM_APENAS_EM_RASCUNHO);

        BigDecimal subtotalItem = request.quantidade().multiply(request.valorUnitario());
        BigDecimal descontoItem = calcularValorDesconto(subtotalItem, request.taxaDesconto());

        VendaOrcamentoItem item = VendaOrcamentoItem.builder()
                .orcamento(orcamento)
                .produtoId(request.produtoId())
                .quantidade(request.quantidade())
                .valorUnitario(request.valorUnitario())
                .taxaDesconto(request.taxaDesconto())
                .valorSubtotal(subtotalItem)
                .valorDesconto(descontoItem)
                .valorTotal(subtotalItem.subtract(descontoItem))
                .build();
        // itemRepository.save(...) explícito (não só orcamento.getItens().add(item) +
        // cascade) — achado real ao validar ao vivo: BaseEntity usa
        // GenerationType.IDENTITY, que só atribui o id no INSERT de verdade; contar só
        // com cascade+dirty-checking deixaria o id null na resposta até o commit da
        // transação (a página só veria o id real num GET subsequente). Mesmo padrão já
        // usado em ComissaoObjetivoService/VendedorRotaService/VendedorMetaService.
        item = itemRepository.save(item);
        orcamento.getItens().add(item);
        recalcularTotais(orcamento);
        return mapper.toResponse(item);
    }

    @Transactional
    public void removerItem(Long orcamentoId, Long itemId) {
        VendaOrcamento orcamento = buscarEntidade(orcamentoId);
        exigirSituacao(orcamento, SituacaoOrcamento.RASCUNHO, VendasErrorCode.ORCAMENTO_ITEM_APENAS_EM_RASCUNHO);
        VendaOrcamentoItem item = buscarItemEntidade(orcamentoId, itemId);
        orcamento.getItens().remove(item);
        recalcularTotais(orcamento);
    }

    @Transactional
    public VendaOrcamentoResponse enviar(Long id) {
        VendaOrcamento orcamento = buscarEntidade(id);
        exigirSituacao(orcamento, SituacaoOrcamento.RASCUNHO, VendasErrorCode.ORCAMENTO_SITUACAO_INVALIDA);
        if (orcamento.getItens().isEmpty()) {
            throw new BusinessException(VendasErrorCode.ORCAMENTO_SEM_ITENS);
        }
        orcamento.setSituacao(SituacaoOrcamento.ENVIADO);
        return mapper.toResponse(orcamento);
    }

    @Transactional
    public VendaOrcamentoResponse aprovar(Long id) {
        VendaOrcamento orcamento = buscarEntidade(id);
        exigirSituacao(orcamento, SituacaoOrcamento.ENVIADO, VendasErrorCode.ORCAMENTO_SITUACAO_INVALIDA);
        orcamento.setSituacao(SituacaoOrcamento.APROVADO);
        return mapper.toResponse(orcamento);
    }

    @Transactional
    public VendaOrcamentoResponse expirar(Long id) {
        VendaOrcamento orcamento = buscarEntidade(id);
        boolean situacaoValida = orcamento.getSituacao() == SituacaoOrcamento.ENVIADO
                || orcamento.getSituacao() == SituacaoOrcamento.APROVADO;
        if (!situacaoValida) {
            throw new ConflictException(VendasErrorCode.ORCAMENTO_SITUACAO_INVALIDA);
        }
        orcamento.setSituacao(SituacaoOrcamento.EXPIRADO);
        return mapper.toResponse(orcamento);
    }

    @Transactional
    public VendaOrcamentoResponse cancelar(Long id) {
        VendaOrcamento orcamento = buscarEntidade(id);
        boolean jaConvertido = orcamento.getSituacao() == SituacaoOrcamento.CONVERTIDO
                || orcamento.getSituacao() == SituacaoOrcamento.CANCELADO;
        if (jaConvertido) {
            throw new ConflictException(VendasErrorCode.ORCAMENTO_SITUACAO_INVALIDA);
        }
        orcamento.setSituacao(SituacaoOrcamento.CANCELADO);
        return mapper.toResponse(orcamento);
    }

    /** Chamado pelo {@link VendaService} depois de gerar a venda a partir deste orçamento. */
    @Transactional
    public void marcarConvertido(Long id) {
        VendaOrcamento orcamento = buscarEntidade(id);
        exigirSituacao(orcamento, SituacaoOrcamento.APROVADO, VendasErrorCode.ORCAMENTO_SITUACAO_INVALIDA);
        orcamento.setSituacao(SituacaoOrcamento.CONVERTIDO);
    }

    private void recalcularTotais(VendaOrcamento orcamento) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal desconto = BigDecimal.ZERO;
        for (VendaOrcamentoItem item : orcamento.getItens()) {
            subtotal = subtotal.add(item.getQuantidade().multiply(item.getValorUnitario()));
            desconto = desconto.add(coalesce(item.getValorDesconto()));
        }
        BigDecimal frete = coalesce(orcamento.getValorFrete());
        BigDecimal comissao = orcamento.getTaxaComissao() != null
                ? subtotal.multiply(orcamento.getTaxaComissao()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                : null;

        orcamento.setValorSubtotal(subtotal);
        orcamento.setValorDesconto(desconto);
        orcamento.setValorComissao(comissao);
        orcamento.setValorTotal(subtotal.subtract(desconto).add(frete));
    }

    private BigDecimal coalesce(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private BigDecimal calcularValorDesconto(BigDecimal valorSubtotal, BigDecimal taxaDesconto) {
        if (taxaDesconto == null || taxaDesconto.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return valorSubtotal.multiply(taxaDesconto).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    private void exigirSituacao(VendaOrcamento orcamento, SituacaoOrcamento esperada, VendasErrorCode erro) {
        if (orcamento.getSituacao() != esperada) {
            throw new ConflictException(erro);
        }
    }

    VendaOrcamentoItem buscarItemEntidade(Long orcamentoId, Long itemId) {
        return itemRepository.findById(itemId)
                .filter(candidato -> candidato.getOrcamento().getId().equals(orcamentoId))
                .orElseThrow(() -> NotFoundException.of("Item de orçamento", itemId));
    }

    VendaOrcamento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Orçamento de venda", id));
    }
}
