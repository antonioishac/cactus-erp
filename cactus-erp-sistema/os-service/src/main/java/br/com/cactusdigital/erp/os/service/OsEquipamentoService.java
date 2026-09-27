package br.com.cactusdigital.erp.os.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.os.dto.OsEquipamentoRequest;
import br.com.cactusdigital.erp.os.dto.OsEquipamentoResponse;
import br.com.cactusdigital.erp.os.entity.OsEquipamento;
import br.com.cactusdigital.erp.os.mapper.OsMapper;
import br.com.cactusdigital.erp.os.repository.OsEquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo de tipo de equipamento — lookup simples, mesmo critério de {@code OsStatusService}. */
@Service
@RequiredArgsConstructor
public class OsEquipamentoService {

    private final OsEquipamentoRepository repository;
    private final OsMapper mapper;

    @Transactional(readOnly = true)
    public Page<OsEquipamentoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public OsEquipamentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public OsEquipamentoResponse criar(OsEquipamentoRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public OsEquipamentoResponse atualizar(Long id, OsEquipamentoRequest request) {
        OsEquipamento entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    OsEquipamento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Tipo de equipamento", id));
    }
}
