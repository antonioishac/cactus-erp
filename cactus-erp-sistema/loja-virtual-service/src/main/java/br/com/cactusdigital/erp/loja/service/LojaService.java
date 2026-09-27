package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.loja.dto.LojaRequest;
import br.com.cactusdigital.erp.loja.dto.LojaResponse;
import br.com.cactusdigital.erp.loja.entity.Loja;
import br.com.cactusdigital.erp.loja.entity.SituacaoAssinatura;
import br.com.cactusdigital.erp.loja.exception.LojaVirtualErrorCode;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.LojaRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Loja — o tenant da plataforma. Ver Javadoc de {@link Loja} pro desenho completo. */
@Service
@RequiredArgsConstructor
public class LojaService {

    private final LojaRepository repository;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public Page<LojaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LojaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LojaResponse criar(LojaRequest request) {
        if (repository.existsBySubdominio(request.subdominio())) {
            throw new ConflictException(LojaVirtualErrorCode.SUBDOMINIO_JA_UTILIZADO);
        }
        Loja loja = mapper.toEntity(request);
        loja.setDataInicioAssinatura(LocalDate.now());
        return mapper.toResponse(repository.save(loja));
    }

    @Transactional
    public LojaResponse atualizar(Long id, LojaRequest request) {
        if (repository.existsBySubdominioAndIdNot(request.subdominio(), id)) {
            throw new ConflictException(LojaVirtualErrorCode.SUBDOMINIO_JA_UTILIZADO);
        }
        Loja loja = buscarEntidade(id);
        mapper.atualizar(request, loja);
        return mapper.toResponse(loja);
    }

    @Transactional
    public LojaResponse cancelarAssinatura(Long id) {
        Loja loja = buscarEntidade(id);
        loja.setSituacaoAssinatura(SituacaoAssinatura.CANCELADA);
        return mapper.toResponse(loja);
    }

    @Transactional
    public LojaResponse reativarAssinatura(Long id) {
        Loja loja = buscarEntidade(id);
        loja.setSituacaoAssinatura(SituacaoAssinatura.ATIVA);
        return mapper.toResponse(loja);
    }

    Loja buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Loja", id));
    }
}
