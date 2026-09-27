package br.com.cactusdigital.erp.crm.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.crm.dto.EstagioFunilRequest;
import br.com.cactusdigital.erp.crm.dto.EstagioFunilResponse;
import br.com.cactusdigital.erp.crm.entity.EstagioFunil;
import br.com.cactusdigital.erp.crm.mapper.CrmMapper;
import br.com.cactusdigital.erp.crm.repository.EstagioFunilRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo livre de estágios do funil de vendas — lookup simples, mesmo critério de {@code OsStatusService} (os-service). */
@Service
@RequiredArgsConstructor
public class EstagioFunilService {

    private final EstagioFunilRepository repository;
    private final CrmMapper mapper;

    @Transactional(readOnly = true)
    public Page<EstagioFunilResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    /** Ordenado por {@code ordem} — usado pela visão de funil (Kanban), que precisa das colunas em sequência. */
    @Transactional(readOnly = true)
    public List<EstagioFunilResponse> listarOrdenado() {
        return repository.findAllByOrderByOrdemAsc().stream().map(mapper::toResponse).toList();
    }

    /** Mesma consulta de {@link #listarOrdenado}, mas devolvendo as entidades — usado por {@code OportunidadeService#visaoFunil} pra evitar buscar de novo por id uma a uma. */
    @Transactional(readOnly = true)
    List<EstagioFunil> listarEntidadesOrdenado() {
        return repository.findAllByOrderByOrdemAsc();
    }

    @Transactional(readOnly = true)
    public EstagioFunilResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EstagioFunilResponse criar(EstagioFunilRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public EstagioFunilResponse atualizar(Long id, EstagioFunilRequest request) {
        EstagioFunil entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    EstagioFunil buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Estágio do funil", id));
    }
}
