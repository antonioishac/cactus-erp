package br.com.cactusdigital.erp.os.dto;

import br.com.cactusdigital.erp.os.entity.TipoItemOs;
import java.math.BigDecimal;

public record OsProdutoServicoResponse(
        Long id,
        TipoItemOs tipo,
        Long produtoId,
        String complemento,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorSubtotal,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal) {
}
