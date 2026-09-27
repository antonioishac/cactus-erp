package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoUnidadeRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoUnidadeResponse;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoUnidade;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoUnidadeRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Lookup simples (sem regra de negócio própria além do CRUD) — por isso não tem uma
 * interface separada, diferente de {@link br.com.cactusdigital.erp.cadastro.cliente.ClienteService}
 * e {@link ProdutoService}: abstrair aqui não traria benefício real, só indireção.
 */
@Service
@RequiredArgsConstructor
public class ProdutoUnidadeService {

    private final ProdutoUnidadeRepository repository;
    private final ProdutoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProdutoUnidadeResponse> listar(String descricao, Pageable pageable) {
        Page<ProdutoUnidade> pagina = StringUtils.hasText(descricao)
                ? repository.findByDescricaoContainingIgnoreCase(descricao, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoUnidadeResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoUnidadeResponse criar(ProdutoUnidadeRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ProdutoUnidadeResponse atualizar(Long id, ProdutoUnidadeRequest request) {
        ProdutoUnidade entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ProdutoUnidade buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Unidade de produto", id));
    }
}
