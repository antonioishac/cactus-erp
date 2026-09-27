package br.com.cactusdigital.erp.fiscal.mapper;

import br.com.cactusdigital.erp.fiscal.dto.CteCabecalhoResponse;
import br.com.cactusdigital.erp.fiscal.dto.CteComponenteResponse;
import br.com.cactusdigital.erp.fiscal.dto.ListaServicoMunicipalRequest;
import br.com.cactusdigital.erp.fiscal.dto.ListaServicoMunicipalResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalConsumidorItemResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalConsumidorResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalEletronicaItemResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalEletronicaResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalServicoItemResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalServicoResponse;
import br.com.cactusdigital.erp.fiscal.dto.SagaExecucaoPassoResponse;
import br.com.cactusdigital.erp.fiscal.dto.SagaExecucaoResponse;
import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import br.com.cactusdigital.erp.fiscal.entity.CteComponente;
import br.com.cactusdigital.erp.fiscal.entity.ListaServicoMunicipal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidor;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidorItem;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServico;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServicoItem;
import br.com.cactusdigital.erp.fiscal.entity.SagaExecucao;
import br.com.cactusdigital.erp.fiscal.entity.SagaExecucaoPasso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FiscalMapper {

    NotaFiscalEletronicaResponse toResponse(NotaFiscalEletronica notaFiscal);

    NotaFiscalEletronicaItemResponse toResponse(NotaFiscalEletronicaItem item);

    ListaServicoMunicipalResponse toResponse(ListaServicoMunicipal listaServico);

    /** Lookup simples (sem FK pra resolver) mapeia request -> entidade direto — mesmo padrão de {@code ProdutoMapper.toEntity(ProdutoUnidadeRequest)}. */
    @Mapping(target = "id", ignore = true)
    ListaServicoMunicipal toEntity(ListaServicoMunicipalRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ListaServicoMunicipalRequest request, @MappingTarget ListaServicoMunicipal entity);

    NotaFiscalServicoResponse toResponse(NotaFiscalServico notaFiscalServico);

    @Mapping(target = "listaServicoId", source = "listaServico.id")
    @Mapping(target = "listaServicoDescricao", source = "listaServico.descricao")
    NotaFiscalServicoItemResponse toResponse(NotaFiscalServicoItem item);

    CteCabecalhoResponse toResponse(CteCabecalho cte);

    CteComponenteResponse toResponse(CteComponente componente);

    NotaFiscalConsumidorResponse toResponse(NotaFiscalConsumidor nota);

    NotaFiscalConsumidorItemResponse toResponse(NotaFiscalConsumidorItem item);

    SagaExecucaoResponse toResponse(SagaExecucao sagaExecucao);

    SagaExecucaoPassoResponse toResponse(SagaExecucaoPasso passo);
}
