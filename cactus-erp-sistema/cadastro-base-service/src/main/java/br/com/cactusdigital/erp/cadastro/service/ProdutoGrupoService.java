package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoGrupoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoGrupoResponse;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoGrupo;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoGrupoRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProdutoGrupoService {

    private final ProdutoGrupoRepository repository;
    private final ProdutoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProdutoGrupoResponse> listar(String nome, Pageable pageable) {
        Page<ProdutoGrupo> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoGrupoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoGrupoResponse criar(ProdutoGrupoRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ProdutoGrupoResponse atualizar(Long id, ProdutoGrupoRequest request) {
        ProdutoGrupo entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ProdutoGrupo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Grupo de produto", id));
    }
}
