package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.SituacaoComissao;
import br.com.cactusdigital.erp.vendas.entity.TipoPagamentoVenda;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaComissaoResponse(
        Long id,
        Long vendaId,
        Long vendedorId,
        BigDecimal valorVenda,
        TipoPagamentoVenda tipoContabil,
        BigDecimal valorComissao,
        SituacaoComissao situacao,
        LocalDate dataLancamento) {
}
