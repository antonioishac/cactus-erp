package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoSubgrupoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoSubgrupoResponse;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoGrupo;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoSubgrupo;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoGrupoRepository;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoSubgrupoRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProdutoSubgrupoService {

    private final ProdutoSubgrupoRepository repository;
    private final ProdutoGrupoRepository produtoGrupoRepository;
    private final ProdutoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProdutoSubgrupoResponse> listar(Long produtoGrupoId, String nome, Pageable pageable) {
        Page<ProdutoSubgrupo> pagina;
        if (produtoGrupoId != null && StringUtils.hasText(nome)) {
            pagina = repository.findByProdutoGrupoIdAndNomeContainingIgnoreCase(produtoGrupoId, nome, pageable);
        } else if (produtoGrupoId != null) {
            pagina = repository.findByProdutoGrupoId(produtoGrupoId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoSubgrupoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoSubgrupoResponse criar(ProdutoSubgrupoRequest request) {
        ProdutoSubgrupo entidade = ProdutoSubgrupo.builder()
                .produtoGrupo(buscarGrupo(request.produtoGrupoId()))
                .nome(request.nome())
                .descricao(request.descricao())
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public ProdutoSubgrupoResponse atualizar(Long id, ProdutoSubgrupoRequest request) {
        ProdutoSubgrupo entidade = buscarEntidade(id);
        entidade.setProdutoGrupo(buscarGrupo(request.produtoGrupoId()));
        entidade.setNome(request.nome());
        entidade.setDescricao(request.descricao());
        entidade.setSituacao(request.situacao());
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ProdutoSubgrupo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Subgrupo de produto", id));
    }

    private ProdutoGrupo buscarGrupo(Long produtoGrupoId) {
        return produtoGrupoRepository.findById(produtoGrupoId)
                .orElseThrow(() -> NotFoundException.of("Grupo de produto", produtoGrupoId));
    }
}
