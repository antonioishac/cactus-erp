package br.com.cactusdigital.erp.financeiro.dto;

public record ExtratoBancarioImportacaoResponse(
        Long id,
        Long contaBancariaId,
        String nomeArquivo,
        int quantidadeItens,
        int quantidadeDuplicados,
        Long usuarioId) {
}
