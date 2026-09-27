package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.NotNull;

public record TrocarEmpresaRequest(@NotNull Long empresaId) {
}
