package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioPapelEmpresaRequest(@NotNull Long papelId, @NotNull Long empresaId) {
}
