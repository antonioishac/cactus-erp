package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ConfiguracaoOperacionalLojaRequest(
        boolean permiteMesas,
        boolean permiteComandas,
        boolean permiteDelivery,
        @Size(max = 1) String modoComanda,
        boolean permiteMesaAvulsa,
        @DecimalMin("0") BigDecimal limiteDescontoItemPercentual,
        @DecimalMin("0") BigDecimal limiteDescontoVendaPercentual,
        @DecimalMin("0") BigDecimal limiteDescontoVendaValor,
        @DecimalMin("0") BigDecimal limiteAcrescimoItemPercentual,
        @DecimalMin("0") BigDecimal limiteAcrescimoVendaPercentual,
        boolean permiteDescontoAcimaLimite) {
}
