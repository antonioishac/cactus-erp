package br.com.cactusdigital.erp.os.mapper;

import br.com.cactusdigital.erp.os.dto.OsAberturaEquipamentoResponse;
import br.com.cactusdigital.erp.os.dto.OsAberturaResponse;
import br.com.cactusdigital.erp.os.dto.OsEquipamentoRequest;
import br.com.cactusdigital.erp.os.dto.OsEquipamentoResponse;
import br.com.cactusdigital.erp.os.dto.OsEvolucaoResponse;
import br.com.cactusdigital.erp.os.dto.OsProdutoServicoResponse;
import br.com.cactusdigital.erp.os.dto.OsStatusRequest;
import br.com.cactusdigital.erp.os.dto.OsStatusResponse;
import br.com.cactusdigital.erp.os.entity.OsAbertura;
import br.com.cactusdigital.erp.os.entity.OsAberturaEquipamento;
import br.com.cactusdigital.erp.os.entity.OsEquipamento;
import br.com.cactusdigital.erp.os.entity.OsEvolucao;
import br.com.cactusdigital.erp.os.entity.OsProdutoServico;
import br.com.cactusdigital.erp.os.entity.OsStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OsMapper {

    OsStatusResponse toResponse(OsStatus status);

    /** Lookup simples (sem FK pra resolver) mapeia request -> entidade direto — mesmo padrão de {@code ProdutoMapper.toEntity(ProdutoUnidadeRequest)}. */
    @Mapping(target = "id", ignore = true)
    OsStatus toEntity(OsStatusRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(OsStatusRequest request, @MappingTarget OsStatus entity);

    OsEquipamentoResponse toResponse(OsEquipamento equipamento);

    @Mapping(target = "id", ignore = true)
    OsEquipamento toEntity(OsEquipamentoRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(OsEquipamentoRequest request, @MappingTarget OsEquipamento entity);

    @Mapping(target = "osStatusId", source = "osStatus.id")
    @Mapping(target = "osStatusNome", source = "osStatus.nome")
    OsAberturaResponse toResponse(OsAbertura osAbertura);

    @Mapping(target = "osEquipamentoId", source = "osEquipamento.id")
    @Mapping(target = "osEquipamentoNome", source = "osEquipamento.nome")
    OsAberturaEquipamentoResponse toResponse(OsAberturaEquipamento equipamento);

    OsProdutoServicoResponse toResponse(OsProdutoServico item);

    OsEvolucaoResponse toResponse(OsEvolucao evolucao);
}
