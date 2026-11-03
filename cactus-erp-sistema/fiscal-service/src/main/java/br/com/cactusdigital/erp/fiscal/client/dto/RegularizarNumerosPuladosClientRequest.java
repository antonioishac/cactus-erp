package br.com.cactusdigital.erp.fiscal.client.dto;

/** Espelha {@code RegularizarNumerosFiscaisPuladosRequest} do cadastro-base-service. */
public record RegularizarNumerosPuladosClientRequest(
        String tipoDocumento,
        String serie,
        Long numeroInicial,
        Long numeroFinal,
        String protocoloInutilizacao) {
}
