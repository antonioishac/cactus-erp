package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.NcmRequest;
import br.com.cactusdigital.erp.cadastro.dto.NcmResponse;
import br.com.cactusdigital.erp.cadastro.entity.Ncm;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoCatalogoMapper;
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
public class NcmService {

    private final NcmRepository repository;
    private final TributacaoCatalogoMapper mapper;

    @Transactional(readOnly = true)
    public Page<NcmResponse> listar(String busca, Pageable pageable) {
        Page<Ncm> pagina = StringUtils.hasText(busca)
                ? repository.findByCodigoContainingOrDescricaoContainingIgnoreCase(busca, busca, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NcmResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public NcmResponse criar(NcmRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public NcmResponse atualizar(Long id, NcmRequest request) {
        Ncm entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Ncm buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("NCM", id));
    }
}
