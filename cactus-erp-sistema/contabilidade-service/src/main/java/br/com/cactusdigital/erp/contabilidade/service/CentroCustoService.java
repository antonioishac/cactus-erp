package br.com.cactusdigital.erp.contabilidade.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.contabilidade.dto.CentroCustoRequest;
import br.com.cactusdigital.erp.contabilidade.dto.CentroCustoResponse;
import br.com.cactusdigital.erp.contabilidade.entity.CentroCusto;
import br.com.cactusdigital.erp.contabilidade.exception.ContabilidadeErrorCode;
import br.com.cactusdigital.erp.contabilidade.mapper.ContabilidadeMapper;
import br.com.cactusdigital.erp.contabilidade.repository.CentroCustoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo flat por empresa — mesmo molde de {@code Cargo}/{@code Setor} (cadastro-base-service). */
@Service
@RequiredArgsConstructor
public class CentroCustoService {

    private final CentroCustoRepository repository;
    private final ContabilidadeMapper mapper;

    @Transactional(readOnly = true)
    public Page<CentroCustoResponse> listar(Long empresaId, Pageable pageable) {
        return repository.findByEmpresaId(empresaId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<CentroCustoResponse> listarTodos(Long empresaId) {
        return repository.findByEmpresaIdOrderByCodigoAsc(empresaId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CentroCustoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public CentroCustoResponse criar(CentroCustoRequest request) {
        validarCodigoUnico(request.empresaId(), request.codigo(), null);
        CentroCusto centroCusto = mapper.toEntity(request);
        return mapper.toResponse(repository.save(centroCusto));
    }

    @Transactional
    public CentroCustoResponse atualizar(Long id, CentroCustoRequest request) {
        CentroCusto centroCusto = buscarEntidade(id);
        validarCodigoUnico(request.empresaId(), request.codigo(), centroCusto.getCodigo());
        mapper.atualizar(request, centroCusto);
        return mapper.toResponse(centroCusto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    CentroCusto buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Centro de custo", id));
    }

    private void validarCodigoUnico(Long empresaId, String codigoNovo, String codigoAtual) {
        boolean mudouCodigo = codigoAtual == null || !codigoAtual.equals(codigoNovo);
        if (mudouCodigo && repository.existsByEmpresaIdAndCodigo(empresaId, codigoNovo)) {
            throw new ConflictException(ContabilidadeErrorCode.CENTRO_CUSTO_CODIGO_DUPLICADO);
        }
    }
}
