package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoMarcaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoMarcaResponse;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoMarca;
import br.com.cactusdigital.erp.cadastro.mapper.ProdutoMapper;
import br.com.cactusdigital.erp.cadastro.repository.ProdutoMarcaRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProdutoMarcaService {

    private final ProdutoMarcaRepository repository;
    private final ProdutoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProdutoMarcaResponse> listar(String nome, Pageable pageable) {
        Page<ProdutoMarca> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProdutoMarcaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoMarcaResponse criar(ProdutoMarcaRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ProdutoMarcaResponse atualizar(Long id, ProdutoMarcaRequest request) {
        ProdutoMarca entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ProdutoMarca buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Marca de produto", id));
    }
}
