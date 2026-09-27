package br.com.cactusdigital.erp.crm.mapper;

import br.com.cactusdigital.erp.crm.dto.AtividadeResponse;
import br.com.cactusdigital.erp.crm.dto.EstagioFunilRequest;
import br.com.cactusdigital.erp.crm.dto.EstagioFunilResponse;
import br.com.cactusdigital.erp.crm.dto.InteracaoResponse;
import br.com.cactusdigital.erp.crm.dto.LeadRequest;
import br.com.cactusdigital.erp.crm.dto.LeadResponse;
import br.com.cactusdigital.erp.crm.dto.OportunidadeResponse;
import br.com.cactusdigital.erp.crm.entity.Atividade;
import br.com.cactusdigital.erp.crm.entity.EstagioFunil;
import br.com.cactusdigital.erp.crm.entity.Interacao;
import br.com.cactusdigital.erp.crm.entity.Lead;
import br.com.cactusdigital.erp.crm.entity.Oportunidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CrmMapper {

    EstagioFunilResponse toResponse(EstagioFunil estagio);

    /** Lookup simples (sem FK pra resolver) mapeia request -> entidade direto — mesmo padrão de {@code OsMapper.toEntity(OsStatusRequest)}. */
    @Mapping(target = "id", ignore = true)
    EstagioFunil toEntity(EstagioFunilRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(EstagioFunilRequest request, @MappingTarget EstagioFunil entity);

    LeadResponse toResponse(Lead lead);

    @Mapping(target = "id", ignore = true)
    Lead toEntity(LeadRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(LeadRequest request, @MappingTarget Lead entity);

    @Mapping(target = "leadId", source = "lead.id")
    @Mapping(target = "estagioFunilId", source = "estagioFunil.id")
    @Mapping(target = "estagioFunilNome", source = "estagioFunil.nome")
    OportunidadeResponse toResponse(Oportunidade oportunidade);

    @Mapping(target = "leadId", source = "lead.id")
    @Mapping(target = "oportunidadeId", source = "oportunidade.id")
    AtividadeResponse toResponse(Atividade atividade);

    @Mapping(target = "leadId", source = "lead.id")
    @Mapping(target = "oportunidadeId", source = "oportunidade.id")
    InteracaoResponse toResponse(Interacao interacao);
}
