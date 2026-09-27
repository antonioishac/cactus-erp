package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.fiscal.dto.ListaServicoMunicipalRequest;
import br.com.cactusdigital.erp.fiscal.dto.ListaServicoMunicipalResponse;
import br.com.cactusdigital.erp.fiscal.entity.ListaServicoMunicipal;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.ListaServicoMunicipalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo de item da lista de serviço municipal — lookup simples, mesmo critério de {@code OsStatusService} (os-service). */
@Service
@RequiredArgsConstructor
public class ListaServicoMunicipalService {

    private final ListaServicoMunicipalRepository repository;
    private final FiscalMapper mapper;

    @Transactional(readOnly = true)
    public Page<ListaServicoMunicipalResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ListaServicoMunicipalResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ListaServicoMunicipalResponse criar(ListaServicoMunicipalRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ListaServicoMunicipalResponse atualizar(Long id, ListaServicoMunicipalRequest request) {
        ListaServicoMunicipal entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    ListaServicoMunicipal buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Item da lista de serviço municipal", id));
    }
}
