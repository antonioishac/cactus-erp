package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.TipoEncerramentoFunil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EstagioFunilRequest(
        @Size(max = 30) String codigo,
        @NotBlank @Size(max = 100) String nome,
        @NotNull Integer ordem,
        @NotNull TipoEncerramentoFunil tipoEncerramento) {
}
