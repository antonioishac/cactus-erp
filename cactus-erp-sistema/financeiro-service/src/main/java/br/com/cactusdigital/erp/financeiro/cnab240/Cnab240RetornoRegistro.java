package br.com.cactusdigital.erp.financeiro.cnab240;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Um título já casado (segmento T + segmento U seguinte) extraído de um retorno CNAB240. */
public record Cnab240RetornoRegistro(
        String nossoNumero,
        String numeroDocumento,
        LocalDate dataVencimento,
        BigDecimal valorTitulo,
        String codigoMovimento,
        LocalDate dataOcorrencia,
        BigDecimal valorPago) {
}
