package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.TipoPedidoCompra;
import jakarta.validation.constraints.NotNull;

/** {@code colaboradorId} do pedido gerado é herdado do solicitante da requisição de
 * origem — não precisa ser informado aqui. */
public record GerarPedidoDeCotacaoRequest(
        @NotNull Long empresaId,
        @NotNull TipoPedidoCompra tipo) {
}
