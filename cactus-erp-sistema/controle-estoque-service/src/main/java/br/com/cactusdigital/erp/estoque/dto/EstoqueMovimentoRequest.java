package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.DirecaoAjuste;
import br.com.cactusdigital.erp.estoque.entity.OrigemMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * {@code usuarioId} não vem daqui — é extraído do JWT autenticado no controller (quem
 * fez o lançamento tem que vir do token, nunca de um campo que o cliente preenche).
 * {@code direcaoAjuste} só é obrigatório quando {@code tipoMovimento=AJUSTE}.
 * {@code aprovadorId} só é obrigatório quando o valor (quantidade × custoUnitario, se
 * houver) excede {@code ConfiguracaoEstoqueEmpresa.limiteAprovacaoAjuste} num AJUSTE/PERDA.
 */
public record EstoqueMovimentoRequest(
        @NotNull Long produtoId,
        @NotNull Long depositoId,
        Long loteId,
        Long numeroSerieId,
        @NotNull TipoMovimentoEstoque tipoMovimento,
        DirecaoAjuste direcaoAjuste,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal quantidade,
        BigDecimal custoUnitario,
        @NotNull OrigemMovimentoEstoque origemTipo,
        Long origemId,
        Long aprovadorId,
        @Size(max = 250) String observacao) {
}
