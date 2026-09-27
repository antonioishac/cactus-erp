package br.com.cactusdigital.erp.vendas.mapper;

import br.com.cactusdigital.erp.vendas.dto.ComissaoObjetivoResponse;
import br.com.cactusdigital.erp.vendas.dto.ComissaoPerfilResponse;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalModeloResponse;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalTipoResponse;
import br.com.cactusdigital.erp.vendas.dto.SagaExecucaoPassoResponse;
import br.com.cactusdigital.erp.vendas.dto.SagaExecucaoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesParcelaDTO;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesPagamentoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaComissaoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaFreteResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaResponse;
import br.com.cactusdigital.erp.vendas.dto.VendedorMetaResponse;
import br.com.cactusdigital.erp.vendas.dto.VendedorResponse;
import br.com.cactusdigital.erp.vendas.dto.VendedorRotaResponse;
import br.com.cactusdigital.erp.vendas.entity.ComissaoObjetivo;
import br.com.cactusdigital.erp.vendas.entity.ComissaoPerfil;
import br.com.cactusdigital.erp.vendas.entity.NotaFiscalModelo;
import br.com.cactusdigital.erp.vendas.entity.NotaFiscalTipo;
import br.com.cactusdigital.erp.vendas.entity.SagaExecucao;
import br.com.cactusdigital.erp.vendas.entity.SagaExecucaoPasso;
import br.com.cactusdigital.erp.vendas.entity.VendaCabecalho;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesParcela;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesPagamento;
import br.com.cactusdigital.erp.vendas.entity.VendaComissao;
import br.com.cactusdigital.erp.vendas.entity.VendaFrete;
import br.com.cactusdigital.erp.vendas.entity.VendaItem;
import br.com.cactusdigital.erp.vendas.entity.VendaOrcamento;
import br.com.cactusdigital.erp.vendas.entity.VendaOrcamentoItem;
import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import br.com.cactusdigital.erp.vendas.entity.VendedorMeta;
import br.com.cactusdigital.erp.vendas.entity.VendedorRota;
import br.com.cactusdigital.erp.vendas.dto.ComissaoPerfilRequest;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalModeloRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VendasMapper {

    ComissaoPerfilResponse toResponse(ComissaoPerfil comissaoPerfil);

    SagaExecucaoResponse toResponse(SagaExecucao sagaExecucao);

    SagaExecucaoPassoResponse toResponse(SagaExecucaoPasso passo);

    /** Lookups simples (sem FK pra resolver) mapeiam request -> entidade direto aqui —
     * mesmo padrão de {@code ProdutoMapper.toEntity(ProdutoUnidadeRequest)}. */
    @Mapping(target = "id", ignore = true)
    ComissaoPerfil toEntity(ComissaoPerfilRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ComissaoPerfilRequest request, @MappingTarget ComissaoPerfil entity);

    @Mapping(target = "id", ignore = true)
    NotaFiscalModelo toEntity(NotaFiscalModeloRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(NotaFiscalModeloRequest request, @MappingTarget NotaFiscalModelo entity);

    @Mapping(target = "comissaoPerfilId", source = "comissaoPerfil.id")
    @Mapping(target = "comissaoPerfilNome", source = "comissaoPerfil.nome")
    VendedorResponse toResponse(Vendedor vendedor);

    @Mapping(target = "comissaoPerfilId", source = "comissaoPerfil.id")
    ComissaoObjetivoResponse toResponse(ComissaoObjetivo objetivo);

    @Mapping(target = "vendedorId", source = "vendedor.id")
    VendedorRotaResponse toResponse(VendedorRota rota);

    @Mapping(target = "vendedorId", source = "vendedor.id")
    VendedorMetaResponse toResponse(VendedorMeta meta);

    VendaCondicoesParcelaDTO toDTO(VendaCondicoesParcela parcela);

    VendaCondicoesPagamentoResponse toResponse(VendaCondicoesPagamento condicoes);

    NotaFiscalModeloResponse toResponse(NotaFiscalModelo modelo);

    @Mapping(target = "notaFiscalModeloId", source = "notaFiscalModelo.id")
    @Mapping(target = "notaFiscalModeloDescricao", source = "notaFiscalModelo.descricao")
    NotaFiscalTipoResponse toResponse(NotaFiscalTipo tipo);

    @Mapping(target = "condicoesPagamentoId", source = "condicoesPagamento.id")
    @Mapping(target = "vendedorId", source = "vendedor.id")
    VendaOrcamentoResponse toResponse(VendaOrcamento orcamento);

    VendaOrcamentoItemResponse toResponse(VendaOrcamentoItem item);

    @Mapping(target = "vendaOrcamentoId", source = "orcamento.id")
    @Mapping(target = "condicoesPagamentoId", source = "condicoesPagamento.id")
    @Mapping(target = "notaFiscalTipoId", source = "notaFiscalTipo.id")
    @Mapping(target = "vendedorId", source = "vendedor.id")
    VendaResponse toResponse(VendaCabecalho venda);

    VendaItemResponse toResponse(VendaItem item);

    @Mapping(target = "vendaId", source = "venda.id")
    VendaFreteResponse toResponse(VendaFrete frete);

    @Mapping(target = "vendaId", source = "venda.id")
    @Mapping(target = "vendedorId", source = "vendedor.id")
    VendaComissaoResponse toResponse(VendaComissao comissao);
}
