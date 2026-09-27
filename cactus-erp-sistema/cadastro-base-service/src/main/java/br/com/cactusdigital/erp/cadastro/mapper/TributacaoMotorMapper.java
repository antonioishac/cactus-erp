package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.CofinsConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoTributariaResponse;
import br.com.cactusdigital.erp.cadastro.dto.GrupoTributarioRequest;
import br.com.cactusdigital.erp.cadastro.dto.GrupoTributarioResponse;
import br.com.cactusdigital.erp.cadastro.dto.IcmsConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.IpiConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.IssConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.dto.OperacaoFiscalRequest;
import br.com.cactusdigital.erp.cadastro.dto.OperacaoFiscalResponse;
import br.com.cactusdigital.erp.cadastro.dto.PisConfiguracaoDTO;
import br.com.cactusdigital.erp.cadastro.entity.CofinsConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoTributaria;
import br.com.cactusdigital.erp.cadastro.entity.GrupoTributario;
import br.com.cactusdigital.erp.cadastro.entity.IcmsConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.IpiConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.IssConfiguracao;
import br.com.cactusdigital.erp.cadastro.entity.OperacaoFiscal;
import br.com.cactusdigital.erp.cadastro.entity.PisConfiguracao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapeamento do motor tributário: {@link GrupoTributario}, {@link OperacaoFiscal} (+
 * {@link IssConfiguracao} aninhado) e a célula {@link ConfiguracaoTributaria} (+
 * {@link IcmsConfiguracao}/{@link PisConfiguracao}/{@link CofinsConfiguracao}/
 * {@link IpiConfiguracao} aninhados). FKs (cfop, grupoTributario, operacaoFiscal,
 * configuracaoTributaria) são sempre resolvidas no serviço, nunca aqui.
 */
@Mapper(componentModel = "spring")
public interface TributacaoMotorMapper {

    GrupoTributarioResponse toResponse(GrupoTributario entity);

    @Mapping(target = "id", ignore = true)
    GrupoTributario toEntity(GrupoTributarioRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(GrupoTributarioRequest request, @MappingTarget GrupoTributario entity);

    @Mapping(target = "cfopId", source = "cfop.id")
    @Mapping(target = "cfopCodigo", source = "cfop.codigo")
    @Mapping(target = "issConfiguracao", source = "issConfiguracao")
    OperacaoFiscalResponse toResponse(OperacaoFiscal entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cfop", ignore = true)
    @Mapping(target = "issConfiguracao", ignore = true)
    OperacaoFiscal toEntity(OperacaoFiscalRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cfop", ignore = true)
    @Mapping(target = "issConfiguracao", ignore = true)
    void atualizar(OperacaoFiscalRequest request, @MappingTarget OperacaoFiscal entity);

    IssConfiguracaoDTO toDto(IssConfiguracao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operacaoFiscal", ignore = true)
    IssConfiguracao toEntity(IssConfiguracaoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operacaoFiscal", ignore = true)
    void atualizar(IssConfiguracaoDTO dto, @MappingTarget IssConfiguracao entity);

    IcmsConfiguracaoDTO toDto(IcmsConfiguracao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    IcmsConfiguracao toEntity(IcmsConfiguracaoDTO dto);

    PisConfiguracaoDTO toDto(PisConfiguracao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    PisConfiguracao toEntity(PisConfiguracaoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    void atualizar(PisConfiguracaoDTO dto, @MappingTarget PisConfiguracao entity);

    CofinsConfiguracaoDTO toDto(CofinsConfiguracao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    CofinsConfiguracao toEntity(CofinsConfiguracaoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    void atualizar(CofinsConfiguracaoDTO dto, @MappingTarget CofinsConfiguracao entity);

    IpiConfiguracaoDTO toDto(IpiConfiguracao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    IpiConfiguracao toEntity(IpiConfiguracaoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "configuracaoTributaria", ignore = true)
    void atualizar(IpiConfiguracaoDTO dto, @MappingTarget IpiConfiguracao entity);

    @Mapping(target = "grupoTributarioId", source = "grupoTributario.id")
    @Mapping(target = "grupoTributarioNome", source = "grupoTributario.nome")
    @Mapping(target = "operacaoFiscalId", source = "operacaoFiscal.id")
    @Mapping(target = "operacaoFiscalNome", source = "operacaoFiscal.nome")
    ConfiguracaoTributariaResponse toResponse(ConfiguracaoTributaria entity);
}
