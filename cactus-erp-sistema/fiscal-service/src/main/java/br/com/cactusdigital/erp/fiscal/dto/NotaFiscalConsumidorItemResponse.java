package br.com.cactusdigital.erp.fiscal.dto;

import java.math.BigDecimal;

public record NotaFiscalConsumidorItemResponse(
        Long id,
        Integer numeroItem,
        Long varianteProdutoLojaId,
        String descricao,
        String ncm,
        String csosn,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal) {
}
