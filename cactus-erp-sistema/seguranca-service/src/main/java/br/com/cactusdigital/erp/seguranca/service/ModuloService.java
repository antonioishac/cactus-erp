package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.seguranca.dto.ModuloResponse;
import br.com.cactusdigital.erp.seguranca.entity.Modulo;
import br.com.cactusdigital.erp.seguranca.mapper.SegurancaMapper;
import br.com.cactusdigital.erp.seguranca.repository.ModuloRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Só leitura: um {@link Modulo} corresponde a um serviço de fato implantado no
 * monorepo — a lista é semeada via Flyway (ver V5__seed_dados.sql), não criada por
 * chamada de API.
 */
@Service
@RequiredArgsConstructor
public class ModuloService {

    private final ModuloRepository repository;
    private final SegurancaMapper mapper;

    @Transactional(readOnly = true)
    public Page<ModuloResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ModuloResponse buscarPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> NotFoundException.of("Módulo", id)));
    }
}
