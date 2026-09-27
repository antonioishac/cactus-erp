package br.com.cactusdigital.erp.vendas.client;

import java.time.LocalDate;
import java.util.List;

/** Espelha {@code financeiro-service.GerarContasReceberDeVendaRequest} — cópia deliberada, não dependência Java (limite de serviço). */
public record GerarContasReceberClientRequest(
        Long vendaId,
        Long empresaId,
        Long clienteId,
        LocalDate dataEmissao,
        List<ParcelaFinanceiroClientRequest> parcelas) {
}
