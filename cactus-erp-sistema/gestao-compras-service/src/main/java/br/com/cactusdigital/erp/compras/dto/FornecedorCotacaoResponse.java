package br.com.cactusdigital.erp.compras.dto;

import java.math.BigDecimal;
import java.util.List;

public record FornecedorCotacaoResponse(
        Long id,
        Long cotacaoId,
        Long fornecedorId,
        String prazoEntrega,
        String condicoesPagamento,
        BigDecimal valorSubtotal,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal,
        List<FornecedorCotacaoItemResponse> itens) {
}
