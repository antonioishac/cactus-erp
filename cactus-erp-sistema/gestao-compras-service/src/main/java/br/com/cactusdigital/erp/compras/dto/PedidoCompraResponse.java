package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.IndicadorPagamentoNfe;
import br.com.cactusdigital.erp.compras.entity.OrigemPedidoCompra;
import br.com.cactusdigital.erp.compras.entity.SituacaoPedidoCompra;
import br.com.cactusdigital.erp.compras.entity.TipoFrete;
import br.com.cactusdigital.erp.compras.entity.TipoPedidoCompra;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PedidoCompraResponse(
        Long id,
        Long empresaId,
        TipoPedidoCompra tipo,
        Long fornecedorId,
        Long colaboradorId,
        OrigemPedidoCompra origemTipo,
        Long origemId,
        LocalDate dataPedido,
        LocalDate dataPrevistaEntrega,
        LocalDate dataPrevisaoPagamento,
        String localEntrega,
        String localCobranca,
        String contato,
        TipoFrete tipoFrete,
        IndicadorPagamentoNfe indicadorPagamento,
        Integer quantidadeParcelas,
        Integer diaPrimeiroVencimento,
        Integer intervaloEntreParcelas,
        Integer diaFixoParcela,
        BigDecimal valorSubtotal,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorFrete,
        BigDecimal valorSeguro,
        BigDecimal valorOutrasDespesas,
        BigDecimal valorTotal,
        Long aprovadorId,
        Instant dataAprovacao,
        SituacaoPedidoCompra situacao,
        String observacao,
        List<PedidoCompraItemResponse> itens) {
}
