package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.vendas.entity.TipoPagamentoVenda;
import java.math.BigDecimal;
import java.util.List;

public record VendaCondicoesPagamentoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal faturamentoMinimo,
        BigDecimal faturamentoMaximo,
        BigDecimal indiceCorrecao,
        Integer diasTolerancia,
        BigDecimal valorTolerancia,
        Integer prazoMedio,
        TipoPagamentoVenda vistaPrazo,
        List<VendaCondicoesParcelaDTO> parcelas,
        Situacao situacao) {
}
