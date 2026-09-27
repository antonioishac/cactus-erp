package br.com.cactusdigital.erp.contabilidade.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaRequest;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaResponse;
import br.com.cactusdigital.erp.contabilidade.entity.PlanoConta;
import br.com.cactusdigital.erp.contabilidade.exception.ContabilidadeErrorCode;
import br.com.cactusdigital.erp.contabilidade.mapper.ContabilidadeMapper;
import br.com.cactusdigital.erp.contabilidade.repository.PartidaContabilRepository;
import br.com.cactusdigital.erp.contabilidade.repository.PlanoContaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Plano de contas hierárquico — mesmo espírito de {@code PastaDocumentoService}
 * (auto-referência + validação de ciclo ao trocar o pai), aplicado à árvore
 * sintética/analítica de uma contabilidade de verdade.
 */
@Service
@RequiredArgsConstructor
public class PlanoContaService {

    private final PlanoContaRepository repository;
    private final PartidaContabilRepository partidaContabilRepository;
    private final ContabilidadeMapper mapper;

    @Transactional(readOnly = true)
    public Page<PlanoContaResponse> listar(Long empresaId, Pageable pageable) {
        return repository.findByEmpresaId(empresaId, pageable).map(mapper::toResponse);
    }

    /** Sem paginação, pra popular `Select` no frontend — catálogo pequeno por empresa (mesmo padrão de `size=200` já usado em todo lookup do monorepo). */
    @Transactional(readOnly = true)
    public List<PlanoContaResponse> listarTodas(Long empresaId) {
        return repository.findByEmpresaIdOrderByCodigoAsc(empresaId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanoContaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PlanoContaResponse criar(PlanoContaRequest request) {
        validarCodigoUnico(request.empresaId(), request.codigo(), null);
        PlanoConta contaPai = resolverContaPai(request.empresaId(), request.contaPaiId());

        PlanoConta conta = mapper.toEntity(request);
        conta.setContaPai(contaPai);
        return mapper.toResponse(repository.save(conta));
    }

    @Transactional
    public PlanoContaResponse atualizar(Long id, PlanoContaRequest request) {
        PlanoConta conta = buscarEntidade(id);
        validarCodigoUnico(request.empresaId(), request.codigo(), conta.getCodigo());

        PlanoConta novaContaPai = resolverContaPai(request.empresaId(), request.contaPaiId());
        if (novaContaPai != null) {
            validarSemCiclo(conta.getId(), novaContaPai);
        }

        mapper.atualizar(request, conta);
        conta.setContaPai(novaContaPai);
        return mapper.toResponse(conta);
    }

    @Transactional
    public void excluir(Long id) {
        PlanoConta conta = buscarEntidade(id);
        if (repository.existsByContaPaiId(id) || partidaContabilRepository.existsByContaId(id)) {
            throw new ConflictException(ContabilidadeErrorCode.PLANO_CONTA_COM_MOVIMENTO_OU_SUBCONTAS);
        }
        repository.delete(conta);
    }

    PlanoConta buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Conta do plano de contas", id));
    }

    private void validarCodigoUnico(Long empresaId, String codigoNovo, String codigoAtual) {
        boolean mudouCodigo = codigoAtual == null || !codigoAtual.equals(codigoNovo);
        if (mudouCodigo && repository.existsByEmpresaIdAndCodigo(empresaId, codigoNovo)) {
            throw new ConflictException(ContabilidadeErrorCode.PLANO_CONTA_CODIGO_DUPLICADO);
        }
    }

    private PlanoConta resolverContaPai(Long empresaId, Long contaPaiId) {
        if (contaPaiId == null) {
            return null;
        }
        PlanoConta contaPai = buscarEntidade(contaPaiId);
        if (!contaPai.getEmpresaId().equals(empresaId)) {
            throw new BusinessException(ContabilidadeErrorCode.PLANO_CONTA_PAI_DE_OUTRA_EMPRESA);
        }
        if (contaPai.isContaAnalitica()) {
            throw new BusinessException(ContabilidadeErrorCode.PLANO_CONTA_PAI_DEVE_SER_SINTETICA);
        }
        return contaPai;
    }

    /** Sobe a cadeia de ancestrais a partir de {@code novaContaPai} — se {@code contaId} aparecer no caminho, a troca criaria um ciclo. */
    private void validarSemCiclo(Long contaId, PlanoConta novaContaPai) {
        PlanoConta atual = novaContaPai;
        while (atual != null) {
            if (atual.getId().equals(contaId)) {
                throw new BusinessException(ContabilidadeErrorCode.PLANO_CONTA_CICLO_HIERARQUIA);
            }
            atual = atual.getContaPai();
        }
    }
}
