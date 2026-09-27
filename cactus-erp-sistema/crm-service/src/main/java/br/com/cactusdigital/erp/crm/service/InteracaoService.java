package br.com.cactusdigital.erp.crm.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.crm.dto.InteracaoRequest;
import br.com.cactusdigital.erp.crm.dto.InteracaoResponse;
import br.com.cactusdigital.erp.crm.entity.Interacao;
import br.com.cactusdigital.erp.crm.entity.Lead;
import br.com.cactusdigital.erp.crm.entity.Oportunidade;
import br.com.cactusdigital.erp.crm.exception.CrmErrorCode;
import br.com.cactusdigital.erp.crm.mapper.CrmMapper;
import br.com.cactusdigital.erp.crm.repository.InteracaoRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Histórico append-only do que já aconteceu — sem atualizar/excluir, mesmo critério de {@code EstoqueMovimento}/{@code DocumentoVersao}. */
@Service
@RequiredArgsConstructor
public class InteracaoService {

    private final InteracaoRepository repository;
    private final LeadService leadService;
    private final OportunidadeService oportunidadeService;
    private final CrmMapper mapper;

    @Transactional(readOnly = true)
    public Page<InteracaoResponse> listar(Long leadId, Long oportunidadeId, Pageable pageable) {
        if (leadId != null) {
            return repository.findByLead_Id(leadId, pageable).map(mapper::toResponse);
        }
        if (oportunidadeId != null) {
            return repository.findByOportunidade_Id(oportunidadeId, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional
    public InteracaoResponse criar(InteracaoRequest request) {
        if (request.leadId() == null && request.oportunidadeId() == null) {
            throw new BusinessException(CrmErrorCode.INTERACAO_SEM_VINCULO);
        }
        Lead lead = request.leadId() != null ? leadService.buscarEntidade(request.leadId()) : null;
        Oportunidade oportunidade = request.oportunidadeId() != null ? oportunidadeService.buscarEntidade(request.oportunidadeId()) : null;

        Interacao interacao = Interacao.builder()
                .lead(lead)
                .oportunidade(oportunidade)
                .tipo(request.tipo())
                .dataInteracao(request.dataInteracao() != null ? request.dataInteracao() : Instant.now())
                .usuarioId(request.usuarioId())
                .descricao(request.descricao())
                .build();
        return mapper.toResponse(repository.save(interacao));
    }
}
