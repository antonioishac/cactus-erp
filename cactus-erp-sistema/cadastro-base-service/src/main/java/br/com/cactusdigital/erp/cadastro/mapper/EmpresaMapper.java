package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoOperacionalLojaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoOperacionalLojaResponse;
import br.com.cactusdigital.erp.cadastro.dto.ContadorRequest;
import br.com.cactusdigital.erp.cadastro.dto.ContadorResponse;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaConfiguracaoFiscalRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaConfiguracaoFiscalResponse;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaRegimeTributarioResponse;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaRequest;
import br.com.cactusdigital.erp.cadastro.dto.EmpresaResponse;
import br.com.cactusdigital.erp.cadastro.dto.FormaPagamentoRequest;
import br.com.cactusdigital.erp.cadastro.dto.FormaPagamentoResponse;
import br.com.cactusdigital.erp.cadastro.dto.NumeroFiscalPuladoRequest;
import br.com.cactusdigital.erp.cadastro.dto.NumeroFiscalPuladoResponse;
import br.com.cactusdigital.erp.cadastro.entity.ConfiguracaoOperacionalLoja;
import br.com.cactusdigital.erp.cadastro.entity.Contador;
import br.com.cactusdigital.erp.cadastro.entity.Empresa;
import br.com.cactusdigital.erp.cadastro.entity.EmpresaConfiguracaoFiscal;
import br.com.cactusdigital.erp.cadastro.entity.EmpresaRegimeTributario;
import br.com.cactusdigital.erp.cadastro.entity.FormaPagamento;
import br.com.cactusdigital.erp.cadastro.entity.NumeroFiscalPulado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    @Mapping(target = "contadorId", source = "contador.id")
    @Mapping(target = "contadorNome", source = "contador.nome")
    EmpresaResponse toResponse(Empresa empresa);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contador", ignore = true)
    Empresa toEntity(EmpresaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contador", ignore = true)
    void atualizar(EmpresaRequest request, @MappingTarget Empresa entity);

    ContadorResponse toResponse(Contador contador);

    @Mapping(target = "id", ignore = true)
    Contador toEntity(ContadorRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ContadorRequest request, @MappingTarget Contador entity);

    NumeroFiscalPuladoResponse toResponse(NumeroFiscalPulado numeroFiscalPulado);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresaId", ignore = true)
    NumeroFiscalPulado toEntity(NumeroFiscalPuladoRequest request);

    @Mapping(target = "empresaId", source = "empresa.id")
    EmpresaConfiguracaoFiscalResponse toResponse(EmpresaConfiguracaoFiscal configuracao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void atualizar(EmpresaConfiguracaoFiscalRequest request, @MappingTarget EmpresaConfiguracaoFiscal entity);

    @Mapping(target = "empresaId", source = "empresa.id")
    EmpresaRegimeTributarioResponse toResponse(EmpresaRegimeTributario regimeTributario);

    @Mapping(target = "empresaId", source = "empresa.id")
    ConfiguracaoOperacionalLojaResponse toResponse(ConfiguracaoOperacionalLoja configuracao);

    @Mapping(target = "id", ignore = true)
    void atualizar(ConfiguracaoOperacionalLojaRequest request, @MappingTarget ConfiguracaoOperacionalLoja entity);

    FormaPagamentoResponse toResponse(FormaPagamento formaPagamento);

    /** {@code tefTipoGp} é opcional no request mas {@code NOT NULL} na coluna (default
     * {@code NENHUM}) — sem o {@code defaultValue} aqui, o MapStruct copia o {@code null}
     * do request por cima do {@code @Builder.Default} da entidade (mesmo achado real já
     * corrigido em {@code ProdutoImagem.ordem}, Entrega 7 do cactus-erp-web). */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tefTipoGp", source = "tefTipoGp", defaultValue = "NENHUM")
    FormaPagamento toEntity(FormaPagamentoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tefTipoGp", source = "tefTipoGp", defaultValue = "NENHUM")
    void atualizar(FormaPagamentoRequest request, @MappingTarget FormaPagamento entity);
}
