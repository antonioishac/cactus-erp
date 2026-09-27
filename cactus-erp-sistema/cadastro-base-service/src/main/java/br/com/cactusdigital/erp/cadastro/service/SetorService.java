package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.SetorRequest;
import br.com.cactusdigital.erp.cadastro.dto.SetorResponse;
import br.com.cactusdigital.erp.cadastro.entity.Setor;
import br.com.cactusdigital.erp.cadastro.mapper.CargoSetorMapper;
import br.com.cactusdigital.erp.cadastro.repository.SetorRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SetorService {

    private final SetorRepository repository;
    private final CargoSetorMapper mapper;

    @Transactional(readOnly = true)
    public Page<SetorResponse> listar(String nome, Pageable pageable) {
        Page<Setor> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SetorResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public SetorResponse criar(SetorRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public SetorResponse atualizar(Long id, SetorRequest request) {
        Setor entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Setor buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Setor", id));
    }
}
