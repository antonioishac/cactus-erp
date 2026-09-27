package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.DepositoRequest;
import br.com.cactusdigital.erp.estoque.dto.DepositoResponse;
import br.com.cactusdigital.erp.estoque.entity.Deposito;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.DepositoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepositoService {

    private final DepositoRepository repository;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<DepositoResponse> listar(Long empresaId, Pageable pageable) {
        Page<Deposito> pagina = empresaId != null
                ? repository.findByEmpresaId(empresaId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<DepositoResponse> listarAtivosDaEmpresa(Long empresaId) {
        return repository.findByEmpresaIdOrderByCodigoAsc(empresaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepositoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public DepositoResponse criar(DepositoRequest request) {
        if (repository.findByEmpresaIdAndCodigo(request.empresaId(), request.codigo()).isPresent()) {
            throw new ConflictException(EstoqueErrorCode.DEPOSITO_CODIGO_DUPLICADO);
        }
        Deposito deposito = Deposito.builder()
                .empresaId(request.empresaId())
                .codigo(request.codigo())
                .nome(request.nome())
                .tipo(request.tipo())
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(deposito));
    }

    @Transactional
    public DepositoResponse atualizar(Long id, DepositoRequest request) {
        Deposito deposito = buscarEntidade(id);
        repository.findByEmpresaIdAndCodigo(request.empresaId(), request.codigo())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException(EstoqueErrorCode.DEPOSITO_CODIGO_DUPLICADO);
                });
        deposito.setEmpresaId(request.empresaId());
        deposito.setCodigo(request.codigo());
        deposito.setNome(request.nome());
        deposito.setTipo(request.tipo());
        deposito.setSituacao(request.situacao());
        return mapper.toResponse(deposito);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Deposito buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Depósito", id));
    }
}
