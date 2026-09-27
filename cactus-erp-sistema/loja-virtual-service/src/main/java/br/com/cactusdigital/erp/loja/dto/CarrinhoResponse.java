package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.loja.entity.SituacaoCarrinho;
import java.math.BigDecimal;
import java.util.List;

public record CarrinhoResponse(
        Long id,
        Long lojaId,
        String token,
        SituacaoCarrinho situacao,
        BigDecimal valorTotal,
        List<ItemCarrinhoResponse> itens) {
}
