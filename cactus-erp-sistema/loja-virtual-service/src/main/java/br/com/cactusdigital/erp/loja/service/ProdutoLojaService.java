package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.loja.dto.ProdutoLojaRequest;
import br.com.cactusdigital.erp.loja.dto.ProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.entity.ProdutoLoja;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.ProdutoLojaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Produto ("modelo") — ver Javadoc de {@link ProdutoLoja}. Nasce sem variantes — adicione via {@code VarianteProdutoLojaService}. */
@Service
@RequiredArgsConstructor
public class ProdutoLojaService {

    private final ProdutoLojaRepository repository;
    private final CategoriaLojaService categoriaLojaService;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProdutoLojaResponse> listar(Long lojaId, Pageable pageable) {
        Page<ProdutoLoja> pagina = lojaId != null ? repository.findByLojaId(lojaId, pageable) : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, ProdutoLoja::getId, repository::buscarComVariantes, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoLojaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoLojaResponse criar(ProdutoLojaRequest request) {
        ProdutoLoja produto = mapper.toEntity(request);
        produto.setCategoriaLoja(categoriaLojaService.buscarEntidade(request.categoriaLojaId()));
        return mapper.toResponse(repository.save(produto));
    }

    @Transactional
    public ProdutoLojaResponse atualizar(Long id, ProdutoLojaRequest request) {
        ProdutoLoja produto = buscarEntidade(id);
        mapper.atualizar(request, produto);
        produto.setCategoriaLoja(categoriaLojaService.buscarEntidade(request.categoriaLojaId()));
        return mapper.toResponse(produto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    ProdutoLoja buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Produto da loja", id));
    }
}
