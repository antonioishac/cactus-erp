package br.com.cactusdigital.erp.vendas.client;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Espelha {@code financeiro-service.dto.VendaPdvDinheiroRequest} — DTO duplicado de propósito (limite de serviço). */
public record VendaPdvDinheiroClientRequest(
        Long registroId,
        Long empresaId,
        Long pdvCaixaId,
        String pdvCaixaNome,
        BigDecimal valor,
        LocalDate dataVenda,
        String descricao) {
}
