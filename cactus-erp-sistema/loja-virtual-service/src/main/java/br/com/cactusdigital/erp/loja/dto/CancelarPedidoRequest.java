package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarPedidoRequest(
        @NotBlank @Size(max = 250) String motivo) {
}
