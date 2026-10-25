package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;

public record VendaPdvPagamentoResponse(
        Long id,
        Long formaPagamentoId,
        String codigoPagamentoNfce,
        String descricao,
        BigDecimal valor,
        String nsu,
        String rede,
        String cartaoDebitoCredito,
        boolean estornado) {
}
