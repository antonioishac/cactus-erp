package br.com.cactusdigital.erp.cadastro.dto;

import java.math.BigDecimal;

public record IbsCbsConfiguracaoResponse(
        String classificacaoTributaria,
        String cst,
        String descricao,
        BigDecimal percentualReducaoIbs,
        BigDecimal percentualReducaoCbs) {
}
