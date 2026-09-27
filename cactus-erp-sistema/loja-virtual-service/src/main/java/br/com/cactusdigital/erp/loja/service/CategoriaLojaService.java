package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.loja.dto.CategoriaLojaRequest;
import br.com.cactusdigital.erp.loja.dto.CategoriaLojaResponse;
import br.com.cactusdigital.erp.loja.entity.CategoriaLoja;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.CategoriaLojaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Árvore de categorias, por loja — ver Javadoc de {@link CategoriaLoja}. */
@Service
@RequiredArgsConstructor
public class CategoriaLojaService {

    private final CategoriaLojaRepository repository;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public Page<CategoriaLojaResponse> listar(Long lojaId, Pageable pageable) {
        Page<CategoriaLoja> pagina = lojaId != null ? repository.findByLojaId(lojaId, pageable) : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoriaLojaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CategoriaLojaResponse criar(CategoriaLojaRequest request) {
        CategoriaLoja categoria = mapper.toEntity(request);
        if (request.categoriaPaiId() != null) {
            categoria.setCategoriaPai(buscarEntidade(request.categoriaPaiId()));
        }
        if (request.ordem() == null) {
            categoria.setOrdem(0);
        }
        return mapper.toResponse(repository.save(categoria));
    }

    @Transactional
    public CategoriaLojaResponse atualizar(Long id, CategoriaLojaRequest request) {
        CategoriaLoja categoria = buscarEntidade(id);
        mapper.atualizar(request, categoria);
        categoria.setCategoriaPai(request.categoriaPaiId() != null ? buscarEntidade(request.categoriaPaiId()) : null);
        return mapper.toResponse(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    CategoriaLoja buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Categoria da loja", id));
    }
}
