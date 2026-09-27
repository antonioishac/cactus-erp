package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** {@code produtoNome}/{@code produtoCodigoInterno} só vêm preenchidos na resposta, ignorados no request. */
public record TabelaPrecoProdutoDTO(
        @NotNull Long produtoId,
        String produtoNome,
        String produtoCodigoInterno,
        BigDecimal valor,
        BigDecimal percentualAjuste) {
}
