package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record CategoriaLojaResponse(
        Long id,
        Long lojaId,
        Long categoriaPaiId,
        String nome,
        Integer ordem,
        Situacao situacao) {
}
