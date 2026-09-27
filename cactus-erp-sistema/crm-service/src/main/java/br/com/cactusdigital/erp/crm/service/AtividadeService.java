package br.com.cactusdigital.erp.crm.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.crm.dto.AtividadeRequest;
import br.com.cactusdigital.erp.crm.dto.AtividadeResponse;
import br.com.cactusdigital.erp.crm.entity.Atividade;
import br.com.cactusdigital.erp.crm.entity.Lead;
import br.com.cactusdigital.erp.crm.entity.Oportunidade;
import br.com.cactusdigital.erp.crm.exception.CrmErrorCode;
import br.com.cactusdigital.erp.crm.mapper.CrmMapper;
import br.com.cactusdigital.erp.crm.repository.AtividadeRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Tarefa agendada (ligar/reunir/visitar) — ver Javadoc de {@link Atividade} pra diferença com {@code Interacao}. */
@Service
@RequiredArgsConstructor
public class AtividadeService {

    private final AtividadeRepository repository;
    private final LeadService leadService;
    private final OportunidadeService oportunidadeService;
    private final CrmMapper mapper;

    @Transactional(readOnly = true)
    public Page<AtividadeResponse> listar(Long leadId, Long oportunidadeId, Pageable pageable) {
        if (leadId != null) {
            return repository.findByLead_Id(leadId, pageable).map(mapper::toResponse);
        }
        if (oportunidadeId != null) {
            return repository.findByOportunidade_Id(oportunidadeId, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AtividadeResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public AtividadeResponse criar(AtividadeRequest request) {
        if (request.leadId() == null && request.oportunidadeId() == null) {
            throw new BusinessException(CrmErrorCode.ATIVIDADE_SEM_VINCULO);
        }
        Lead lead = request.leadId() != null ? leadService.buscarEntidade(request.leadId()) : null;
        Oportunidade oportunidade = request.oportunidadeId() != null ? oportunidadeService.buscarEntidade(request.oportunidadeId()) : null;

        Atividade atividade = Atividade.builder()
                .lead(lead)
                .oportunidade(oportunidade)
                .tipo(request.tipo())
                .responsavelId(request.responsavelId())
                .dataHora(request.dataHora())
                .descricao(request.descricao())
                .build();
        return mapper.toResponse(repository.save(atividade));
    }

    @Transactional
    public AtividadeResponse concluir(Long id) {
        Atividade atividade = buscarEntidade(id);
        if (atividade.isConcluida()) {
            throw new ConflictException(CrmErrorCode.ATIVIDADE_JA_CONCLUIDA);
        }
        atividade.setConcluida(true);
        atividade.setDataConclusao(Instant.now());
        return mapper.toResponse(atividade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Atividade buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Atividade", id));
    }
}
