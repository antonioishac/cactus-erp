package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.CestRequest;
import br.com.cactusdigital.erp.cadastro.dto.CestResponse;
import br.com.cactusdigital.erp.cadastro.dto.CfopRequest;
import br.com.cactusdigital.erp.cadastro.dto.CfopResponse;
import br.com.cactusdigital.erp.cadastro.dto.NcmRequest;
import br.com.cactusdigital.erp.cadastro.dto.NcmResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cest;
import br.com.cactusdigital.erp.cadastro.entity.Cfop;
import br.com.cactusdigital.erp.cadastro.entity.Ncm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TributacaoCatalogoMapper {

    NcmResponse toResponse(Ncm ncm);

    @Mapping(target = "id", ignore = true)
    Ncm toEntity(NcmRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(NcmRequest request, @MappingTarget Ncm entity);

    CfopResponse toResponse(Cfop cfop);

    @Mapping(target = "id", ignore = true)
    Cfop toEntity(CfopRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(CfopRequest request, @MappingTarget Cfop entity);

    @Mapping(target = "ncmId", source = "ncm.id")
    @Mapping(target = "ncmCodigo", source = "ncm.codigo")
    CestResponse toResponse(Cest cest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ncm", ignore = true)
    Cest toEntity(CestRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ncm", ignore = true)
    void atualizar(CestRequest request, @MappingTarget Cest entity);
}
