package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record ProdutoSubgrupoResponse(
        Long id,
        Long produtoGrupoId,
        String produtoGrupoNome,
        String nome,
        String descricao,
        Situacao situacao) {
}
