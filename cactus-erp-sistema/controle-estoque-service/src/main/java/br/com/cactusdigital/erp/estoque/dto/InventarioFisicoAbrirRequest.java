package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.TipoInventario;
import jakarta.validation.constraints.NotNull;

/** Abrir gera automaticamente um {@code InventarioFisicoItem} por produto com saldo no
 * depósito, com {@code quantidadeSistema} = saldo no momento da abertura. */
public record InventarioFisicoAbrirRequest(
        @NotNull Long empresaId,
        @NotNull Long depositoId,
        @NotNull TipoInventario tipo) {
}
