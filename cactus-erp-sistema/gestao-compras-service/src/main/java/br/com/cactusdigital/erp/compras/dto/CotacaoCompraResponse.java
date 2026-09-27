package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.SituacaoCotacaoCompra;
import java.time.LocalDate;
import java.util.List;

public record CotacaoCompraResponse(
        Long id,
        Long requisicaoId,
        LocalDate dataCotacao,
        String descricao,
        SituacaoCotacaoCompra situacao,
        List<FornecedorCotacaoResponse> fornecedores) {
}
