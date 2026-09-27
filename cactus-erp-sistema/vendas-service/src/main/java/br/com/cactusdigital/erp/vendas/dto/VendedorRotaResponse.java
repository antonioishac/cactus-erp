package br.com.cactusdigital.erp.vendas.dto;

public record VendedorRotaResponse(
        Long id,
        Long vendedorId,
        Long clienteId,
        Integer posicao) {
}
