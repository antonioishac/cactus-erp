package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.EmpresaRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaResponse;
import br.com.cactusdigital.erp.cadastro.entity.Contador;
import br.com.cactusdigital.erp.cadastro.entity.Empresa;
import br.com.cactusdigital.erp.cadastro.mapper.EmpresaMapper;
import br.com.cactusdigital.erp.cadastro.repository.ContadorRepository;
import br.com.cactusdigital.erp.cadastro.repository.EmpresaRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;
    private final ContadorRepository contadorRepository;
    private final EmpresaMapper mapper;

    @Transactional(readOnly = true)
    public Page<EmpresaResponse> listar(String nome, Pageable pageable) {
        Page<Empresa> pagina = StringUtils.hasText(nome)
                ? repository.findByRazaoSocialContainingIgnoreCaseOrNomeFantasiaContainingIgnoreCase(nome, nome, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EmpresaResponse criar(EmpresaRequest request) {
        Empresa entidade = mapper.toEntity(request);
        entidade.setContador(request.contadorId() != null ? buscarContador(request.contadorId()) : null);
        return mapper.toResponse(repository.save(entidade));
    }

    @Transactional
    public EmpresaResponse atualizar(Long id, EmpresaRequest request) {
        Empresa entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        entidade.setContador(request.contadorId() != null ? buscarContador(request.contadorId()) : null);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Empresa buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Empresa", id));
    }

    private Contador buscarContador(Long id) {
        return contadorRepository.findById(id).orElseThrow(() -> NotFoundException.of("Contador", id));
    }
}
