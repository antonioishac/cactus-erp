package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CargoRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 250) String descricao,
        @DecimalMin(value = "0", inclusive = true) BigDecimal salario,
        @Size(max = 10) String cbo1994,
        @Size(max = 10) String cbo2002,
        @NotNull Situacao situacao) {
}
