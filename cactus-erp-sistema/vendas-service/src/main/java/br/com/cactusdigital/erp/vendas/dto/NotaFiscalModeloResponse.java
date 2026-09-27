package br.com.cactusdigital.erp.vendas.dto;

public record NotaFiscalModeloResponse(
        Long id,
        String codigo,
        String descricao,
        String modelo) {
}
