package br.com.cactusdigital.erp.cadastro.dto;

import java.math.BigDecimal;

public record ConfiguracaoOperacionalLojaResponse(
        Long id,
        Long empresaId,
        boolean permiteMesas,
        boolean permiteComandas,
        boolean permiteDelivery,
        String modoComanda,
        boolean permiteMesaAvulsa,
        BigDecimal limiteDescontoItemPercentual,
        BigDecimal limiteDescontoVendaPercentual,
        BigDecimal limiteDescontoVendaValor,
        BigDecimal limiteAcrescimoItemPercentual,
        BigDecimal limiteAcrescimoVendaPercentual,
        boolean permiteDescontoAcimaLimite) {
}
