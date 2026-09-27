package br.com.cactusdigital.erp.vendas.client;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Espelha {@code financeiro-service.ParcelaVendaRequest} — cópia deliberada, não dependência Java (limite de serviço). */
public record ParcelaFinanceiroClientRequest(
        Integer numeroParcela,
        LocalDate dataVencimento,
        BigDecimal valor) {
}
