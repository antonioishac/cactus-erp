package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.loja.entity.TipoAtributo;
import java.util.List;

public record AtributoCategoriaResponse(
        Long id,
        Long categoriaLojaId,
        String nome,
        TipoAtributo tipo,
        boolean obrigatorio,
        boolean geraVariacao,
        Integer ordem,
        List<AtributoCategoriaOpcaoResponse> opcoes) {
}
