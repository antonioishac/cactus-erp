package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.SituacaoAplicacaoFinanceira;
import br.com.cactusdigital.erp.financeiro.entity.TipoAplicacaoFinanceira;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AplicacaoFinanceiraResponse(
        Long id,
        Long contaBancariaId,
        TipoAplicacaoFinanceira tipoAplicacao,
        String descricao,
        LocalDate dataAplicacao,
        BigDecimal valorAplicado,
        LocalDate dataResgate,
        BigDecimal valorResgatado,
        BigDecimal rendimento,
        SituacaoAplicacaoFinanceira situacao) {
}
