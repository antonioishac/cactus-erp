package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.TipoFrete;
import java.math.BigDecimal;

public record VendaFreteRequest(
        Long transportadoraId,
        String conhecimento,
        TipoFrete responsavel,
        String placa,
        String ufPlaca,
        String seloFiscal,
        BigDecimal quantidadeVolume,
        String marcaVolume,
        String especieVolume,
        BigDecimal pesoBruto,
        BigDecimal pesoLiquido) {
}
