package br.com.cactusdigital.erp.os.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.os.dto.OsStatusRequest;
import br.com.cactusdigital.erp.os.dto.OsStatusResponse;
import br.com.cactusdigital.erp.os.entity.OsStatus;
import br.com.cactusdigital.erp.os.mapper.OsMapper;
import br.com.cactusdigital.erp.os.repository.OsStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo livre de status de OS — lookup simples, mesmo critério de {@code ComissaoPerfilService} (vendas-service). */
@Service
@RequiredArgsConstructor
public class OsStatusService {

    private final OsStatusRepository repository;
    private final OsMapper mapper;

    @Transactional(readOnly = true)
    public Page<OsStatusResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public OsStatusResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public OsStatusResponse criar(OsStatusRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public OsStatusResponse atualizar(Long id, OsStatusRequest request) {
        OsStatus entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    OsStatus buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Status de OS", id));
    }
}
