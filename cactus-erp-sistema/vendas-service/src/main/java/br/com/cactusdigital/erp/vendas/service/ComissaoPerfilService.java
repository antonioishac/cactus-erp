package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.ComissaoPerfilRequest;
import br.com.cactusdigital.erp.vendas.dto.ComissaoPerfilResponse;
import br.com.cactusdigital.erp.vendas.entity.ComissaoPerfil;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.ComissaoPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lookup simples (sem regra de negócio própria além do CRUD) — mesmo critério de {@code ProdutoUnidadeService} no cadastro-base. */
@Service
@RequiredArgsConstructor
public class ComissaoPerfilService {

    private final ComissaoPerfilRepository repository;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<ComissaoPerfilResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ComissaoPerfilResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ComissaoPerfilResponse criar(ComissaoPerfilRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ComissaoPerfilResponse atualizar(Long id, ComissaoPerfilRequest request) {
        ComissaoPerfil entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    ComissaoPerfil buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Perfil de comissão", id));
    }
}
