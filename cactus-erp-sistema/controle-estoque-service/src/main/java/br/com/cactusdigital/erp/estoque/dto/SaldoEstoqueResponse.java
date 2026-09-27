package br.com.cactusdigital.erp.estoque.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SaldoEstoqueResponse(
        Long id,
        Long produtoId,
        Long depositoId,
        String depositoNome,
        BigDecimal quantidade,
        BigDecimal quantidadeReservada,
        BigDecimal quantidadeDisponivel,
        BigDecimal custoMedioPonderado,
        Instant dataUltimaMovimentacao) {
}
