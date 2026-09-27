package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenciaBancariaResponse(
        Long id,
        Long contaOrigemId,
        Long contaDestinoId,
        LocalDate dataTransferencia,
        BigDecimal valor,
        String historico,
        Long usuarioId) {
}
