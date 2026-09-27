package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * {@code quantidadeMinima=1} + {@code dataInicio}/{@code dataFim} preenchidos = preço
 * promocional; {@code quantidadeMinima>1} sem vigência = desconto por atacado permanente.
 */
public record ProdutoFaixaPrecoDTO(
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal quantidadeMinima,
        @NotNull @DecimalMin(value = "0", inclusive = true) BigDecimal valorUnitario,
        @Size(max = 50) String rotulo,
        LocalDate dataInicio,
        LocalDate dataFim,
        Situacao situacao) {
}
