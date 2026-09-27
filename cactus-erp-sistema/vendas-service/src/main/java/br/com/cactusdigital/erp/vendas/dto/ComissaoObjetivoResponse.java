package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.TipoPagamentoVenda;
import java.math.BigDecimal;

public record ComissaoObjetivoResponse(
        Long id,
        Long comissaoPerfilId,
        Long produtoId,
        String codigo,
        String nome,
        String descricao,
        TipoPagamentoVenda formaPagamento,
        BigDecimal taxaPagamento,
        BigDecimal valorPagamento,
        BigDecimal valorMeta,
        BigDecimal quantidade) {
}
