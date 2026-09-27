package br.com.cactusdigital.erp.ged.dto;

public record PastaDocumentoResponse(Long id, String nome, String descricao, Long pastaPaiId, String pastaPaiNome) {
}
