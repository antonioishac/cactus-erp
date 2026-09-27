package br.com.cactusdigital.erp.crm.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.crm.dto.ConverterLeadRequest;
import br.com.cactusdigital.erp.crm.dto.DesqualificarLeadRequest;
import br.com.cactusdigital.erp.crm.dto.LeadRequest;
import br.com.cactusdigital.erp.crm.dto.LeadResponse;
import br.com.cactusdigital.erp.crm.entity.Lead;
import br.com.cactusdigital.erp.crm.entity.SituacaoLead;
import br.com.cactusdigital.erp.crm.exception.CrmErrorCode;
import br.com.cactusdigital.erp.crm.mapper.CrmMapper;
import br.com.cactusdigital.erp.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lead — {@code NOVO} → {@code EM_QUALIFICACAO} → {@code QUALIFICADO}/
 * {@code DESQUALIFICADO} → {@code CONVERTIDO}. Conversão só VINCULA um
 * {@code clienteId} já existente — ver Javadoc de {@link Lead} pro porquê de não criar
 * o {@code Cliente} automaticamente.
 */
@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository repository;
    private final CrmMapper mapper;

    @Transactional(readOnly = true)
    public Page<LeadResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LeadResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LeadResponse criar(LeadRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public LeadResponse atualizar(Long id, LeadRequest request) {
        Lead lead = buscarEntidade(id);
        mapper.atualizar(request, lead);
        return mapper.toResponse(lead);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    @Transactional
    public LeadResponse qualificar(Long id) {
        Lead lead = buscarEntidade(id);
        if (lead.getSituacao() != SituacaoLead.NOVO && lead.getSituacao() != SituacaoLead.EM_QUALIFICACAO) {
            throw new ConflictException(CrmErrorCode.LEAD_SITUACAO_INVALIDA_PARA_QUALIFICAR);
        }
        lead.setSituacao(SituacaoLead.QUALIFICADO);
        return mapper.toResponse(lead);
    }

    @Transactional
    public LeadResponse desqualificar(Long id, DesqualificarLeadRequest request) {
        Lead lead = buscarEntidade(id);
        if (lead.getSituacao() == SituacaoLead.DESQUALIFICADO || lead.getSituacao() == SituacaoLead.CONVERTIDO) {
            throw new ConflictException(CrmErrorCode.LEAD_SITUACAO_INVALIDA_PARA_DESQUALIFICAR);
        }
        lead.setSituacao(SituacaoLead.DESQUALIFICADO);
        lead.setMotivoDesqualificacao(request.motivo());
        return mapper.toResponse(lead);
    }

    /** Só vincula {@code clienteId} — não cria o Cliente no cadastro-base-service, ver Javadoc de {@link Lead}. */
    @Transactional
    public LeadResponse converter(Long id, ConverterLeadRequest request) {
        Lead lead = buscarEntidade(id);
        if (lead.getSituacao() == SituacaoLead.CONVERTIDO) {
            throw new ConflictException(CrmErrorCode.LEAD_JA_CONVERTIDO);
        }
        if (lead.getSituacao() != SituacaoLead.QUALIFICADO) {
            throw new BusinessException(CrmErrorCode.LEAD_SITUACAO_INVALIDA_PARA_CONVERTER);
        }
        lead.setSituacao(SituacaoLead.CONVERTIDO);
        lead.setClienteId(request.clienteId());
        return mapper.toResponse(lead);
    }

    Lead buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Lead", id));
    }
}
