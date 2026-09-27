package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.TipoPagamentoVenda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ComissaoObjetivoRequest(
        @NotNull Long produtoId,
        @Size(max = 3) String codigo,
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 250) String descricao,
        TipoPagamentoVenda formaPagamento,
        BigDecimal taxaPagamento,
        BigDecimal valorPagamento,
        BigDecimal valorMeta,
        BigDecimal quantidade) {
}
