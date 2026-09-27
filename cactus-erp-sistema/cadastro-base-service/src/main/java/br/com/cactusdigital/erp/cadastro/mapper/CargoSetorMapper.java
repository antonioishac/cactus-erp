package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.CargoRequest;
import br.com.cactusdigital.erp.cadastro.dto.CargoResponse;
import br.com.cactusdigital.erp.cadastro.dto.SetorRequest;
import br.com.cactusdigital.erp.cadastro.dto.SetorResponse;
import br.com.cactusdigital.erp.cadastro.entity.Cargo;
import br.com.cactusdigital.erp.cadastro.entity.Setor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CargoSetorMapper {

    CargoResponse toResponse(Cargo cargo);

    @Mapping(target = "id", ignore = true)
    Cargo toEntity(CargoRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(CargoRequest request, @MappingTarget Cargo entity);

    SetorResponse toResponse(Setor setor);

    @Mapping(target = "id", ignore = true)
    Setor toEntity(SetorRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(SetorRequest request, @MappingTarget Setor entity);
}
