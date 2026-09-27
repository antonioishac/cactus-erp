package br.com.cactusdigital.erp.ged.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record TipoDocumentoRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotNull @DecimalMin(value = "0.01") BigDecimal tamanhoMaximoMb,
        @NotNull Situacao situacao) {
}
