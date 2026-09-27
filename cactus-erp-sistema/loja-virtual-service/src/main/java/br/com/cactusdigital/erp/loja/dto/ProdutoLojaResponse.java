package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.util.List;

public record ProdutoLojaResponse(
        Long id,
        Long lojaId,
        Long categoriaLojaId,
        String categoriaLojaNome,
        String nome,
        String descricao,
        String marca,
        Situacao situacao,
        List<VarianteProdutoLojaResponse> variantes) {
}
