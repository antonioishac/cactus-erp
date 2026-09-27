package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.vendas.entity.TipoPagamentoVenda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record VendaCondicoesPagamentoRequest(
        @NotBlank @Size(max = 50) String nome,
        @Size(max = 250) String descricao,
        BigDecimal faturamentoMinimo,
        BigDecimal faturamentoMaximo,
        BigDecimal indiceCorrecao,
        Integer diasTolerancia,
        BigDecimal valorTolerancia,
        Integer prazoMedio,
        TipoPagamentoVenda vistaPrazo,
        @Valid List<VendaCondicoesParcelaDTO> parcelas,
        @NotNull Situacao situacao) {
}
