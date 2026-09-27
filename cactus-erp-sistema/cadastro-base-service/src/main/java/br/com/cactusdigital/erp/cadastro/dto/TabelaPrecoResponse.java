package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoAjustePreco;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.Instant;
import java.util.List;

public record TabelaPrecoResponse(
        Long id,
        String nome,
        String descricao,
        TipoAjustePreco tipoAjuste,
        Situacao situacao,
        List<TabelaPrecoProdutoDTO> produtos,
        Instant dataAtualizacao) {
}
