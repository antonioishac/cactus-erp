package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.IndicadorPagamentoNfe;
import br.com.cactusdigital.erp.compras.entity.OrigemPedidoCompra;
import br.com.cactusdigital.erp.compras.entity.TipoFrete;
import br.com.cactusdigital.erp.compras.entity.TipoPedidoCompra;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Itens não vão inline aqui — são adicionados via {@code POST /pedidos/{id}/itens}
 * (uniforme pra pedido avulso, direto de requisição ou gerado a partir de cotação, que
 * já nasce com os itens copiados do fornecedor vencedor). */
public record PedidoCompraRequest(
        @NotNull Long empresaId,
        @NotNull TipoPedidoCompra tipo,
        @NotNull Long fornecedorId,
        @NotNull Long colaboradorId,
        @NotNull OrigemPedidoCompra origemTipo,
        Long origemId,
        LocalDate dataPedido,
        LocalDate dataPrevistaEntrega,
        LocalDate dataPrevisaoPagamento,
        @Size(max = 100) String localEntrega,
        @Size(max = 100) String localCobranca,
        @Size(max = 50) String contato,
        TipoFrete tipoFrete,
        IndicadorPagamentoNfe indicadorPagamento,
        Integer quantidadeParcelas,
        Integer diaPrimeiroVencimento,
        Integer intervaloEntreParcelas,
        Integer diaFixoParcela,
        BigDecimal valorFrete,
        BigDecimal valorSeguro,
        BigDecimal valorOutrasDespesas,
        @Size(max = 250) String observacao) {
}
