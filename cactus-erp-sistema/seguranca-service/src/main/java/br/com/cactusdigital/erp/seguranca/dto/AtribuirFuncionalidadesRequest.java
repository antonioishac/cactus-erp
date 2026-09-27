package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

/** Substitui por completo o conjunto de funcionalidades de um papel (PUT idempotente). */
public record AtribuirFuncionalidadesRequest(@NotNull Set<Long> funcionalidadeIds) {
}
