package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(@NotBlank @Size(min = 8, max = 100) String novaSenha) {
}
