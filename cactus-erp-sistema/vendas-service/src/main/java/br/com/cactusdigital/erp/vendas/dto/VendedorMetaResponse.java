package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.PeriodoMeta;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VendedorMetaResponse(
        Long id,
        Long vendedorId,
        Long clienteId,
        PeriodoMeta periodoMeta,
        BigDecimal metaOrcada,
        BigDecimal metaRealizada,
        LocalDate dataInicio,
        LocalDate dataFim) {
}
