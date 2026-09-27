package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.compras.dto.CotacaoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.CotacaoCompraResponse;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoItemRequest;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoRequest;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoResponse;
import br.com.cactusdigital.erp.compras.entity.CotacaoCompra;
import br.com.cactusdigital.erp.compras.entity.FornecedorCotacao;
import br.com.cactusdigital.erp.compras.entity.FornecedorCotacaoItem;
import br.com.cactusdigital.erp.compras.entity.RequisicaoCompra;
import br.com.cactusdigital.erp.compras.entity.SituacaoCotacaoCompra;
import br.com.cactusdigital.erp.compras.exception.ComprasErrorCode;
import br.com.cactusdigital.erp.compras.mapper.ComprasMapper;
import br.com.cactusdigital.erp.compras.repository.CotacaoCompraRepository;
import br.com.cactusdigital.erp.compras.repository.FornecedorCotacaoItemRepository;
import br.com.cactusdigital.erp.compras.repository.FornecedorCotacaoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cotação de compra — vinculada a uma {@link RequisicaoCompra}, agrega N fornecedores
 * concorrentes. {@code valorSubtotal}/{@code valorDesconto}/{@code valorTotal} de cada
 * {@link FornecedorCotacao} são sempre recalculados a partir dos itens, nunca informados
 * diretamente pelo cliente da API.
 */
@Service
@RequiredArgsConstructor
public class CotacaoCompraService {

    private final CotacaoCompraRepository repository;
    private final FornecedorCotacaoRepository fornecedorCotacaoRepository;
    private final FornecedorCotacaoItemRepository fornecedorCotacaoItemRepository;
    private final RequisicaoCompraService requisicaoCompraService;
    private final ComprasMapper mapper;

    @Transactional(readOnly = true)
    public Page<CotacaoCompraResponse> listarPorRequisicao(Long requisicaoId, Pageable pageable) {
        Page<CotacaoCompra> pagina = repository.findByRequisicaoId(requisicaoId, pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, CotacaoCompra::getId, this::buscarComFornecedoresEItens, mapper::toResponse);
    }

    /** Duas queries dentro da mesma sessão: a 1ª busca as cotações com {@code fornecedores}
     * já carregado; a 2ª hidrata {@code itens} de cada fornecedor por efeito colateral no
     * persistence context (não dá pra fazer as duas coisas numa query só — Hibernate
     * lança {@code MultipleBagFetchException} pra duas coleções {@code List} buscadas
     * juntas, mesmo encadeadas). Ao final, navegar {@code cotacao.getFornecedores().get(i)
     * .getItens()} não dispara SELECT extra, porque a 2ª query já populou essas coleções
     * no mesmo contexto de persistência. */
    private List<CotacaoCompra> buscarComFornecedoresEItens(List<Long> ids) {
        List<CotacaoCompra> comFornecedores = repository.buscarComFornecedores(ids);
        fornecedorCotacaoRepository.carregarItensPorCotacaoIds(ids);
        return comFornecedores;
    }

    @Transactional(readOnly = true)
    public CotacaoCompraResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CotacaoCompraResponse criar(CotacaoCompraRequest request) {
        RequisicaoCompra requisicao = requisicaoCompraService.buscarEntidade(request.requisicaoId());

        CotacaoCompra cotacao = CotacaoCompra.builder()
                .requisicao(requisicao)
                .dataCotacao(request.dataCotacao())
                .descricao(request.descricao())
                .situacao(SituacaoCotacaoCompra.ABERTA)
                .build();
        return mapper.toResponse(repository.save(cotacao));
    }

    @Transactional
    public CotacaoCompraResponse cancelar(Long id) {
        CotacaoCompra cotacao = buscarEntidade(id);
        exigirSituacao(cotacao, SituacaoCotacaoCompra.ABERTA);
        cotacao.setSituacao(SituacaoCotacaoCompra.CANCELADA);
        return mapper.toResponse(cotacao);
    }

    /** Chamado pelo {@code PedidoCompraService} quando um pedido nasce a partir de um
     * dos fornecedores concorrentes desta cotação — encerra o próprio ciclo dela. */
    @Transactional
    public void finalizar(Long id) {
        buscarEntidade(id).setSituacao(SituacaoCotacaoCompra.FINALIZADA);
    }

    @Transactional
    public FornecedorCotacaoResponse adicionarFornecedor(Long cotacaoId, FornecedorCotacaoRequest request) {
        CotacaoCompra cotacao = buscarEntidade(cotacaoId);
        exigirSituacao(cotacao, SituacaoCotacaoCompra.ABERTA);

        FornecedorCotacao fornecedorCotacao = FornecedorCotacao.builder()
                .cotacao(cotacao)
                .fornecedorId(request.fornecedorId())
                .prazoEntrega(request.prazoEntrega())
                .condicoesPagamento(request.condicoesPagamento())
                .build();
        cotacao.getFornecedores().add(fornecedorCotacao);
        return mapper.toResponse(fornecedorCotacaoRepository.save(fornecedorCotacao));
    }

    @Transactional
    public FornecedorCotacaoResponse adicionarItem(Long cotacaoId, Long fornecedorCotacaoId, FornecedorCotacaoItemRequest request) {
        CotacaoCompra cotacao = buscarEntidade(cotacaoId);
        exigirSituacao(cotacao, SituacaoCotacaoCompra.ABERTA);
        FornecedorCotacao fornecedorCotacao = buscarFornecedorCotacao(cotacaoId, fornecedorCotacaoId);

        BigDecimal valorSubtotalItem = request.quantidade().multiply(request.valorUnitario());
        BigDecimal valorDescontoItem = calcularValorDesconto(valorSubtotalItem, request.taxaDesconto());

        FornecedorCotacaoItem item = FornecedorCotacaoItem.builder()
                .fornecedorCotacao(fornecedorCotacao)
                .produtoId(request.produtoId())
                .quantidade(request.quantidade())
                .valorUnitario(request.valorUnitario())
                .taxaDesconto(request.taxaDesconto())
                .valorDesconto(valorDescontoItem)
                .valorTotal(valorSubtotalItem.subtract(valorDescontoItem))
                .build();
        // save explícito, mesmo achado real do vendas-service (Fase 7) e de
        // PedidoCompraService#adicionarItem — GenerationType.IDENTITY só atribui o id
        // no INSERT de verdade, cascade+dirty-checking sozinho deixaria item.id=null
        // nesta resposta (o pai, fornecedorCotacao, já é uma entidade gerenciada).
        item = fornecedorCotacaoItemRepository.save(item);
        fornecedorCotacao.getItens().add(item);

        recalcularTotais(fornecedorCotacao);
        return mapper.toResponse(fornecedorCotacao);
    }

    private void recalcularTotais(FornecedorCotacao fornecedorCotacao) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal desconto = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (FornecedorCotacaoItem item : fornecedorCotacao.getItens()) {
            subtotal = subtotal.add(item.getQuantidade().multiply(item.getValorUnitario()));
            desconto = desconto.add(item.getValorDesconto());
            total = total.add(item.getValorTotal());
        }
        fornecedorCotacao.setValorSubtotal(subtotal);
        fornecedorCotacao.setValorDesconto(desconto);
        fornecedorCotacao.setValorTotal(total);
    }

    private BigDecimal calcularValorDesconto(BigDecimal valorSubtotal, BigDecimal taxaDesconto) {
        if (taxaDesconto == null || taxaDesconto.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return valorSubtotal.multiply(taxaDesconto)
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    private void exigirSituacao(CotacaoCompra cotacao, SituacaoCotacaoCompra esperada) {
        if (cotacao.getSituacao() != esperada) {
            throw new ConflictException(ComprasErrorCode.COTACAO_SITUACAO_INVALIDA);
        }
    }

    FornecedorCotacao buscarFornecedorCotacao(Long cotacaoId, Long fornecedorCotacaoId) {
        return fornecedorCotacaoRepository.findById(fornecedorCotacaoId)
                .filter(candidato -> candidato.getCotacao().getId().equals(cotacaoId))
                .orElseThrow(() -> NotFoundException.of("Fornecedor da cotação", fornecedorCotacaoId));
    }

    CotacaoCompra buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Cotação de compra", id));
    }
}
