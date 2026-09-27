package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.fiscal.dto.SagaExecucaoResponse;
import br.com.cactusdigital.erp.fiscal.entity.SagaExecucao;
import br.com.cactusdigital.erp.fiscal.mapper.FiscalMapper;
import br.com.cactusdigital.erp.fiscal.repository.SagaExecucaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta only — a linha de {@link SagaExecucao} nasce inteiramente de dentro de
 * {@code NotaFiscalEletronicaService#gerarDeVenda} (via {@code NotaFiscalSagaAuditor}),
 * nunca por criação/edição manual via API. Mesmo desenho de
 * {@code vendas-service}/{@code gestao-compras-service.SagaExecucaoService}.
 */
@Service
@RequiredArgsConstructor
public class SagaExecucaoService {

    private final SagaExecucaoRepository repository;
    private final FiscalMapper mapper;

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
