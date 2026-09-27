package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.seguranca.dto.FuncionalidadeResponse;
import br.com.cactusdigital.erp.seguranca.entity.Funcionalidade;
import br.com.cactusdigital.erp.seguranca.mapper.SegurancaMapper;
import br.com.cactusdigital.erp.seguranca.repository.FuncionalidadeRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Só leitura: cada serviço registra as próprias {@link Funcionalidade}s via migration
 * ao ganhar um endpoint protegido — não são criadas por chamada de API.
 */
@Service
@RequiredArgsConstructor
public class FuncionalidadeService {

    private final FuncionalidadeRepository repository;
    private final SegurancaMapper mapper;

    @Transactional(readOnly = true)
    public Page<FuncionalidadeResponse> listar(Long moduloId, Pageable pageable) {
        Page<Funcionalidade> pagina = moduloId != null
                ? repository.findByModuloId(moduloId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FuncionalidadeResponse buscarPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> NotFoundException.of("Funcionalidade", id)));
    }
}
