package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.DirecaoAjuste;
import br.com.cactusdigital.erp.estoque.entity.OrigemMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import java.math.BigDecimal;
import java.time.Instant;

public record EstoqueMovimentoResponse(
        Long id,
        Long produtoId,
        Long depositoId,
        Long loteId,
        Long numeroSerieId,
        TipoMovimentoEstoque tipoMovimento,
        DirecaoAjuste direcaoAjuste,
        BigDecimal quantidade,
        BigDecimal quantidadeAnterior,
        BigDecimal quantidadeNova,
        BigDecimal custoUnitario,
        OrigemMovimentoEstoque origemTipo,
        Long origemId,
        Long usuarioId,
        Long aprovadorId,
        String observacao,
        Instant dataMovimento) {
}
