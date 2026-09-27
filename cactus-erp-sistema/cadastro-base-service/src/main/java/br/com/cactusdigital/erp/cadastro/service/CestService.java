package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.CestRequest;
import br.com.cactusdigital.erp.cadastro.dto.CestResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cest;
import br.com.cactusdigital.erp.cadastro.entity.Ncm;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoCatalogoMapper;
import br.com.cactusdigital.erp.cadastro.repository.CestRepository;
import br.com.cactusdigital.erp.cadastro.repository.NcmRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CestService {

    private final CestRepository repository;
    private final NcmRepository ncmRepository;
    private final TributacaoCatalogoMapper mapper;

    @Transactional(readOnly = true)
    public Page<CestResponse> listar(String descricao, Pageable pageable) {
        Page<Cest> pagina = StringUtils.hasText(descricao)
                ? repository.findByDescricaoContainingIgnoreCase(descricao, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CestResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CestResponse criar(CestRequest request) {
        Cest entidade = mapper.toEntity(request);
        entidade.setNcm(request.ncmId() != null ? buscarNcm(request.ncmId()) : null);
        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public CestResponse atualizar(Long id, CestRequest request) {
        Cest entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        entidade.setNcm(request.ncmId() != null ? buscarNcm(request.ncmId()) : null);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Cest buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("CEST", id));
    }

    private Ncm buscarNcm(Long id) {
        return ncmRepository.findById(id).orElseThrow(() -> NotFoundException.of("NCM", id));
    }
}
