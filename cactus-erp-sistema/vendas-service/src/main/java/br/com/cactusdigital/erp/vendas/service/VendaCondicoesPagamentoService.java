package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesParcelaDTO;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesPagamentoRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesPagamentoResponse;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesParcela;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesPagamento;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.VendaCondicoesPagamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Condição de pagamento + parcelas — parcelas são substituídas por inteiro a cada
 * atualização ("clear e reconstrói", mesmo padrão de
 * {@code ProdutoServiceImpl#aplicarFaixasPreco}/{@code PessoaAssembler}).
 */
@Service
@RequiredArgsConstructor
public class VendaCondicoesPagamentoService {

    private final VendaCondicoesPagamentoRepository repository;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<VendaCondicoesPagamentoResponse> listar(Pageable pageable) {
        Page<VendaCondicoesPagamento> pagina = repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, VendaCondicoesPagamento::getId, repository::buscarComParcelas, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VendaCondicoesPagamentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public VendaCondicoesPagamentoResponse criar(VendaCondicoesPagamentoRequest request) {
        VendaCondicoesPagamento condicoes = VendaCondicoesPagamento.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .faturamentoMinimo(request.faturamentoMinimo())
                .faturamentoMaximo(request.faturamentoMaximo())
                .indiceCorrecao(request.indiceCorrecao())
                .diasTolerancia(request.diasTolerancia())
                .valorTolerancia(request.valorTolerancia())
                .prazoMedio(request.prazoMedio())
                .vistaPrazo(request.vistaPrazo())
                .situacao(request.situacao())
                .build();
        aplicarParcelas(condicoes, request.parcelas());
        return mapper.toResponse(repository.save(condicoes));
    }

    @Transactional
    public VendaCondicoesPagamentoResponse atualizar(Long id, VendaCondicoesPagamentoRequest request) {
        VendaCondicoesPagamento condicoes = buscarEntidade(id);
        condicoes.setNome(request.nome());
        condicoes.setDescricao(request.descricao());
        condicoes.setFaturamentoMinimo(request.faturamentoMinimo());
        condicoes.setFaturamentoMaximo(request.faturamentoMaximo());
        condicoes.setIndiceCorrecao(request.indiceCorrecao());
        condicoes.setDiasTolerancia(request.diasTolerancia());
        condicoes.setValorTolerancia(request.valorTolerancia());
        condicoes.setPrazoMedio(request.prazoMedio());
        condicoes.setVistaPrazo(request.vistaPrazo());
        condicoes.setSituacao(request.situacao());
        aplicarParcelas(condicoes, request.parcelas());
        return mapper.toResponse(condicoes);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private void aplicarParcelas(VendaCondicoesPagamento condicoes, List<VendaCondicoesParcelaDTO> parcelas) {
        condicoes.getParcelas().clear();
        for (VendaCondicoesParcelaDTO dto : parcelas != null ? parcelas : List.<VendaCondicoesParcelaDTO>of()) {
            condicoes.getParcelas().add(VendaCondicoesParcela.builder()
                    .condicoesPagamento(condicoes)
                    .parcela(dto.parcela())
                    .dias(dto.dias())
                    .taxa(dto.taxa())
                    .build());
        }
    }

    VendaCondicoesPagamento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Condição de pagamento", id));
    }
}
