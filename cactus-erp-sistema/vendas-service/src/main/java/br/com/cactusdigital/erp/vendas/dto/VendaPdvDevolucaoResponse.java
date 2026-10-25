package br.com.cactusdigital.erp.vendas.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record VendaPdvDevolucaoResponse(
        Long id,
        Long vendaPdvItemId,
        Integer numeroItem,
        BigDecimal quantidadeDevolvida,
        BigDecimal valorDevolvido,
        String motivo,
        Long pdvOperadorId,
        Long pdvGerenteAutorizadorId,
        Instant dataHoraDevolucao) {
}
