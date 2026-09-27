package br.com.cactusdigital.erp.compras.mapper;

import br.com.cactusdigital.erp.compras.dto.ConfiguracaoComprasEmpresaResponse;
import br.com.cactusdigital.erp.compras.dto.CotacaoCompraResponse;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoItemResponse;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoResponse;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraItemResponse;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraResponse;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraItemResponse;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraResponse;
import br.com.cactusdigital.erp.compras.dto.RequisicaoCompraItemResponse;
import br.com.cactusdigital.erp.compras.dto.RequisicaoCompraResponse;
import br.com.cactusdigital.erp.compras.dto.SagaExecucaoPassoResponse;
import br.com.cactusdigital.erp.compras.dto.SagaExecucaoResponse;
import br.com.cactusdigital.erp.compras.entity.ConfiguracaoComprasEmpresa;
import br.com.cactusdigital.erp.compras.entity.CotacaoCompra;
import br.com.cactusdigital.erp.compras.entity.FornecedorCotacao;
import br.com.cactusdigital.erp.compras.entity.FornecedorCotacaoItem;
import br.com.cactusdigital.erp.compras.entity.PedidoCompra;
import br.com.cactusdigital.erp.compras.entity.PedidoCompraItem;
import br.com.cactusdigital.erp.compras.entity.RecebimentoCompra;
import br.com.cactusdigital.erp.compras.entity.RecebimentoCompraItem;
import br.com.cactusdigital.erp.compras.entity.RequisicaoCompra;
import br.com.cactusdigital.erp.compras.entity.RequisicaoCompraItem;
import br.com.cactusdigital.erp.compras.entity.SagaExecucao;
import br.com.cactusdigital.erp.compras.entity.SagaExecucaoPasso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComprasMapper {

    RequisicaoCompraItemResponse toResponse(RequisicaoCompraItem item);

    SagaExecucaoResponse toResponse(SagaExecucao sagaExecucao);

    SagaExecucaoPassoResponse toResponse(SagaExecucaoPasso passo);

    RequisicaoCompraResponse toResponse(RequisicaoCompra requisicao);

    @Mapping(target = "requisicaoId", source = "requisicao.id")
    CotacaoCompraResponse toResponse(CotacaoCompra cotacao);

    @Mapping(target = "cotacaoId", source = "cotacao.id")
    FornecedorCotacaoResponse toResponse(FornecedorCotacao fornecedorCotacao);

    FornecedorCotacaoItemResponse toResponse(FornecedorCotacaoItem item);

    PedidoCompraResponse toResponse(PedidoCompra pedido);

    @Mapping(target = "quantidadePendente", expression = "java(item.getQuantidadePendente())")
    PedidoCompraItemResponse toResponse(PedidoCompraItem item);

    ConfiguracaoComprasEmpresaResponse toResponse(ConfiguracaoComprasEmpresa configuracao);

    @Mapping(target = "pedidoId", source = "pedido.id")
    RecebimentoCompraResponse toResponse(RecebimentoCompra recebimento);

    @Mapping(target = "pedidoCompraItemId", source = "pedidoCompraItem.id")
    @Mapping(target = "produtoId", source = "pedidoCompraItem.produtoId")
    RecebimentoCompraItemResponse toResponse(RecebimentoCompraItem item);
}
