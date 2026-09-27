package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoTipoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoTipoResponse;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoTipo;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoTipoRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProdutoTipoService {

    private final ProdutoTipoRepository repository;
    private final ProdutoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProdutoTipoResponse> listar(String descricao, Pageable pageable) {
        Page<ProdutoTipo> pagina = StringUtils.hasText(descricao)
                ? repository.findByDescricaoContainingIgnoreCase(descricao, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoTipoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoTipoResponse criar(ProdutoTipoRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ProdutoTipoResponse atualizar(Long id, ProdutoTipoRequest request) {
        ProdutoTipo entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ProdutoTipo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Tipo de produto", id));
    }
}
