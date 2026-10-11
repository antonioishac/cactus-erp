package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.SituacaoPedidoCompra;
import br.com.cactusdigital.erp.compras.entity.TipoPedidoCompra;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoCompraRelatorioItemResponse(
        Long pedidoId,
        LocalDate dataPedido,
        Long empresaId,
        Long fornecedorId,
        TipoPedidoCompra tipo,
        SituacaoPedidoCompra situacao,
        Integer quantidadeItens,
        BigDecimal valorTotal) {
}
