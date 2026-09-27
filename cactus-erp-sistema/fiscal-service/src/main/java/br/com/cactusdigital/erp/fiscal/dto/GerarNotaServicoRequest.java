package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * {@code listaServicoId} é aplicado a TODOS os itens {@code SERVICO} da OS de origem —
 * simplificação deliberada (uma OS pode ter mão de obra de naturezas diferentes com
 * códigos de serviço municipal distintos; mapear item a item fica pra quando isso virar
 * necessidade real, registrado no CLAUDE.md).
 */
public record GerarNotaServicoRequest(
        @NotBlank @Size(max = 15) String numeroRps,
        @Size(max = 5) String serieRps,
        @NotBlank @Size(max = 6) String competencia,
        @NotNull Long listaServicoId) {
}
