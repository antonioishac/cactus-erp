package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.contabilidade.entity.TipoPartida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PartidaContabilRequest(
        @NotNull Long contaId,
        Long centroCustoId,
        @NotNull TipoPartida tipoPartida,
        @NotNull @DecimalMin(value = "0.000001", message = "O valor da partida precisa ser maior que zero") BigDecimal valor,
        @Size(max = 250) String historicoComplementar) {
}
