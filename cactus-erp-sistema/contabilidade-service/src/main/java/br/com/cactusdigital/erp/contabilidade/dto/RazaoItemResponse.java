package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.contabilidade.entity.TipoPartida;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Uma linha do Livro Razão de uma conta — {@code saldoCorrido} acumula na ordem cronológica (ver {@code RelatorioContabilService#razao}). */
public record RazaoItemResponse(
        Long lancamentoContabilId,
        LocalDate dataLancamento,
        String historico,
        TipoPartida tipoPartida,
        BigDecimal valor,
        BigDecimal saldoCorrido) {
}
