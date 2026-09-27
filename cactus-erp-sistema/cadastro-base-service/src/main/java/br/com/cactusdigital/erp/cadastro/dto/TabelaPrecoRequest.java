package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoAjustePreco;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TabelaPrecoRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 250) String descricao,
        @NotNull TipoAjustePreco tipoAjuste,
        @NotNull Situacao situacao,
        @Valid List<TabelaPrecoProdutoDTO> produtos) {
}
