package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.vendas.dto.SagaExecucaoResponse;
import br.com.cactusdigital.erp.vendas.entity.SagaExecucao;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.SagaExecucaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta only — a linha de {@link SagaExecucao} nasce inteiramente de dentro de
 * {@code VendaService#confirmar} (via {@code VendaSagaAuditor}), nunca por criação/edição
 * manual via API. Existe pra dar visibilidade operacional sobre execuções de saga que
 * falharam na compensação ({@code situacao=FALHA_COMPENSACAO}) — é o sinal de
 * "intervenção manual necessária" já registrado no Javadoc de {@code SagaOrchestrator},
 * exposto de um jeito consultável em vez de só um log.
 */
@Service
@RequiredArgsConstructor
public class SagaExecucaoService {

    private final SagaExecucaoRepository repository;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<SagaExecucaoResponse> listar(String referenciaTipo, Long referenciaId, Pageable pageable) {
        Page<SagaExecucao> pagina = (referenciaTipo != null && referenciaId != null)
                ? repository.findByReferenciaTipoAndReferenciaId(referenciaTipo, referenciaId, pageable)
                : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, SagaExecucao::getId, repository::buscarComPassos, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SagaExecucaoResponse buscarPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> NotFoundException.of("Execução de saga", id)));
    }
}
