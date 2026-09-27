package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExtratoBancarioItemResponse(
        Long id,
        Long importacaoId,
        Long contaBancariaId,
        LocalDate dataMovimento,
        String historico,
        String tipoOfx,
        BigDecimal valor,
        String fitId,
        boolean conciliado,
        Long movimentoCaixaBancoId) {
}
