package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.GrupoTributarioRequest;
import br.com.cactusdigital.erp.cadastro.dto.GrupoTributarioResponse;
import br.com.cactusdigital.erp.cadastro.entity.GrupoTributario;
import br.com.cactusdigital.erp.cadastro.mapper.TributacaoMotorMapper;
import br.com.cactusdigital.erp.cadastro.repository.GrupoTributarioRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * CRUD do perfil tributário do produto ({@link GrupoTributario}) — validado contra
 * {@code fenix.sql.TRIBUT_GRUPO_TRIBUTARIO}. Ver ANÁLISE TRIBUTÁRIA no CLAUDE.md.
 */
@Service
@RequiredArgsConstructor
public class GrupoTributarioService {

    private final GrupoTributarioRepository repository;
    private final TributacaoMotorMapper mapper;

    @Transactional(readOnly = true)
    public Page<GrupoTributarioResponse> listar(String nome, Pageable pageable) {
        Page<GrupoTributario> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public GrupoTributarioResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public GrupoTributarioResponse criar(GrupoTributarioRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public GrupoTributarioResponse atualizar(Long id, GrupoTributarioRequest request) {
        GrupoTributario entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private GrupoTributario buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Grupo tributário", id));
    }
}
