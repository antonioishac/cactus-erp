package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.PeriodoMeta;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VendedorMetaRequest(
        @NotNull Long clienteId,
        PeriodoMeta periodoMeta,
        BigDecimal metaOrcada,
        LocalDate dataInicio,
        LocalDate dataFim) {
}
