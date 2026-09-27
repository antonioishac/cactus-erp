package br.com.cactusdigital.erp.bi.dto;

import java.math.BigDecimal;

public record ComprasFornecedorIndicadorResponse(
        String anoMes,
        Long fornecedorId,
        String fornecedorNome,
        Integer quantidadePedidos,
        BigDecimal valorTotalComprado) {
}
