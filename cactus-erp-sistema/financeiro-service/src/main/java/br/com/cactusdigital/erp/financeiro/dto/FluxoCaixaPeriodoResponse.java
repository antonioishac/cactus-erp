package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Um período (dia ou mês, ver {@link AgrupamentoFluxoCaixa}) do fluxo de caixa
 * PROJETADO — soma de {@code valorSaldo} de títulos ainda em aberto (ABERTA/PARCIAL),
 * não é fluxo de caixa realizado (não olha baixas já pagas). {@code saldoAcumulado} é a
 * soma corrida de {@code saldoPeriodo} desde o primeiro período do intervalo consultado.
 */
public record FluxoCaixaPeriodoResponse(
        LocalDate periodo,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldoPeriodo,
        BigDecimal saldoAcumulado) {
}
