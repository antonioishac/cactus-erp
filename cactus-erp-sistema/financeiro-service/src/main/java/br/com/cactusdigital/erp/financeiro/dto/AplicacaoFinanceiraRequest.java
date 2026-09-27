package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoAplicacaoFinanceira;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AplicacaoFinanceiraRequest(
        @NotNull Long contaBancariaId,
        @NotNull TipoAplicacaoFinanceira tipoAplicacao,
        @Size(max = 150) String descricao,
        LocalDate dataAplicacao,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valorAplicado) {
}
