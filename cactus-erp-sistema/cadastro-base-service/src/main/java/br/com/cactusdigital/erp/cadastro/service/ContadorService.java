package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ContadorRequest;
import br.com.cactusdigital.erp.cadastro.dto.ContadorResponse;
import br.com.cactusdigital.erp.cadastro.entity.Contador;
import br.com.cactusdigital.erp.cadastro.mapper.EmpresaMapper;
import br.com.cactusdigital.erp.cadastro.repository.ContadorRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ContadorService {

    private final ContadorRepository repository;
    private final EmpresaMapper mapper;

    @Transactional(readOnly = true)
    public Page<ContadorResponse> listar(String nome, Pageable pageable) {
        Page<Contador> pagina = StringUtils.hasText(nome)
                ? repository.findByNomeContainingIgnoreCase(nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ContadorResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ContadorResponse criar(ContadorRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public ContadorResponse atualizar(Long id, ContadorRequest request) {
        Contador entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Contador buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Contador", id));
    }
}
