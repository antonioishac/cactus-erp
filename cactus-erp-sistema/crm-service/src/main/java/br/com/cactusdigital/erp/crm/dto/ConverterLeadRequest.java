package br.com.cactusdigital.erp.crm.dto;

import jakarta.validation.constraints.NotNull;

/** {@code clienteId} referencia um {@code Cliente} JÁ cadastrado no cadastro-base-service — ver Javadoc de {@code Lead}. */
public record ConverterLeadRequest(
        @NotNull Long clienteId) {
}
