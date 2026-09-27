package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RemessaBancariaItemResponse(
        Long id,
        Long contaReceberId,
        String nossoNumero,
        String numeroDocumento,
        LocalDate dataVencimento,
        BigDecimal valorTitulo,
        String sacadoNome) {
}
