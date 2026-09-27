package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record ProdutoUnidadeResponse(
        Long id,
        String sigla,
        String descricao,
        boolean podeFracionar,
        Situacao situacao) {
}
