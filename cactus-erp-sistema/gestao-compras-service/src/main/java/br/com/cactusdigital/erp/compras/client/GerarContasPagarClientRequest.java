package br.com.cactusdigital.erp.compras.client;

import java.time.LocalDate;
import java.util.List;

/** Espelha {@code financeiro-service.GerarContasPagarDeCompraRequest} — cópia deliberada, não dependência Java (limite de serviço). */
public record GerarContasPagarClientRequest(
        Long pedidoCompraId,
        Long empresaId,
        Long fornecedorId,
        LocalDate dataEmissao,
        List<ParcelaFinanceiroClientRequest> parcelas) {
}
