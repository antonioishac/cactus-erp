package br.com.cactusdigital.erp.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DesqualificarLeadRequest(
        @NotBlank @Size(max = 250) String motivo) {
}
