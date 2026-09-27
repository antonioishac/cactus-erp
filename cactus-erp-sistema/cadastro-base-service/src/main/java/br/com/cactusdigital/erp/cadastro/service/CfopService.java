package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.CfopRequest;
import br.com.cactusdigital.erp.cadastro.dto.CfopResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cfop;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoCatalogoMapper;
import br.com.cactusdigital.erp.cadastro.repository.CfopRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CfopService {

    private final CfopRepository repository;
    private final TributacaoCatalogoMapper mapper;

    @Transactional(readOnly = true)
    public Page<CfopResponse> listar(String descricao, Pageable pageable) {
        Page<Cfop> pagina = StringUtils.hasText(descricao)
                ? repository.findByDescricaoContainingIgnoreCase(descricao, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CfopResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CfopResponse criar(CfopRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public CfopResponse atualizar(Long id, CfopRequest request) {
        Cfop entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Cfop buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("CFOP", id));
    }
}
