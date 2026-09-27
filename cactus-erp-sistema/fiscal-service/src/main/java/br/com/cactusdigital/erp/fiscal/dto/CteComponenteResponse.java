package br.com.cactusdigital.erp.fiscal.dto;

import java.math.BigDecimal;

public record CteComponenteResponse(
        Long id,
        String nome,
        BigDecimal valor) {
}
