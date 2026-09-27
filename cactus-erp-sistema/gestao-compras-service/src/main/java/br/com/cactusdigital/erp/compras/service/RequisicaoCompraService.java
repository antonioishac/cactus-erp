package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.compras.dto.RequisicaoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.RequisicaoCompraResponse;
import br.com.cactusdigital.erp.compras.entity.RequisicaoCompra;
import br.com.cactusdigital.erp.compras.entity.RequisicaoCompraItem;
import br.com.cactusdigital.erp.compras.entity.SituacaoRequisicaoCompra;
import br.com.cactusdigital.erp.compras.exception.ComprasErrorCode;
import br.com.cactusdigital.erp.compras.mapper.ComprasMapper;
import br.com.cactusdigital.erp.compras.repository.RequisicaoCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequisicaoCompraService {

    private final RequisicaoCompraRepository repository;
    private final ComprasMapper mapper;

    @Transactional(readOnly = true)
    public Page<RequisicaoCompraResponse> listar(Long colaboradorId, Pageable pageable) {
        Page<RequisicaoCompra> pagina = colaboradorId != null
                ? repository.findByColaboradorId(colaboradorId, pageable)
                : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, RequisicaoCompra::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public RequisicaoCompraResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public RequisicaoCompraResponse criar(RequisicaoCompraRequest request) {
        RequisicaoCompra requisicao = RequisicaoCompra.builder()
                .colaboradorId(request.colaboradorId())
                .tipo(request.tipo())
                .descricao(request.descricao())
                .dataRequisicao(request.dataRequisicao())
                .situacao(SituacaoRequisicaoCompra.ABERTA)
                .observacao(request.observacao())
                .build();

        request.itens().forEach(itemRequest -> requisicao.getItens().add(RequisicaoCompraItem.builder()
                .requisicao(requisicao)
                .produtoId(itemRequest.produtoId())
                .quantidade(itemRequest.quantidade())
                .build()));

        return mapper.toResponse(repository.save(requisicao));
    }

    @Transactional
    public RequisicaoCompraResponse cancelar(Long id) {
        RequisicaoCompra requisicao = buscarEntidade(id);
        if (requisicao.getSituacao() == SituacaoRequisicaoCompra.PEDIDO_GERADO) {
            throw new ConflictException(ComprasErrorCode.REQUISICAO_SITUACAO_INVALIDA);
        }
        requisicao.setSituacao(SituacaoRequisicaoCompra.CANCELADA);
        return mapper.toResponse(requisicao);
    }

    /** Chamado pelo {@code PedidoCompraService} quando um pedido nasce direto desta
     * requisição ou de uma cotação vinculada a ela. */
    @Transactional
    public void marcarPedidoGerado(Long id) {
        buscarEntidade(id).setSituacao(SituacaoRequisicaoCompra.PEDIDO_GERADO);
    }

    RequisicaoCompra buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Requisição de compra", id));
    }
}
