package br.com.cactusdigital.erp.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtributoCategoriaOpcaoRequest(
        @NotBlank @Size(max = 60) String valor,
        Integer ordem) {
}
