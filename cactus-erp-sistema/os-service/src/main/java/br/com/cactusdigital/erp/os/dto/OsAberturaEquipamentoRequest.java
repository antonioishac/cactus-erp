package br.com.cactusdigital.erp.os.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OsAberturaEquipamentoRequest(
        @NotNull Long osEquipamentoId,
        @Size(max = 50) String numeroSerie,
        @Size(max = 1) String tipoCobertura) {
}
