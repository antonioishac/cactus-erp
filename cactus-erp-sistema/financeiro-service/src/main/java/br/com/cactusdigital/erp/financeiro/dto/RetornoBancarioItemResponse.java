package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RetornoBancarioItemResponse(
        Long id,
        String nossoNumero,
        String numeroDocumento,
        String codigoMovimento,
        LocalDate dataOcorrencia,
        BigDecimal valorTitulo,
        BigDecimal valorPago,
        Long contaReceberId,
        boolean processado,
        String observacao) {
}
