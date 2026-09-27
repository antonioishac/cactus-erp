package br.com.cactusdigital.erp.financeiro.dto;

import java.util.List;

public record RetornoBancarioResponse(
        Long id,
        Long contaBancariaId,
        String nomeArquivo,
        int quantidadeRegistros,
        List<RetornoBancarioItemResponse> itens) {
}
