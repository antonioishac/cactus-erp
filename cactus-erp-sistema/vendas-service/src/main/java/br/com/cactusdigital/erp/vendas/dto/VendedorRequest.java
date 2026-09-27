package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VendedorRequest(
        @NotNull Long colaboradorId,
        Long comissaoPerfilId,
        BigDecimal comissaoPercentual,
        BigDecimal metaVenda,
        @NotNull Situacao situacao) {
}
