package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.SituacaoVenda;
import br.com.cactusdigital.erp.vendas.entity.TipoFrete;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record VendaResponse(
        Long id,
        Long empresaId,
        Long vendaOrcamentoId,
        Long condicoesPagamentoId,
        Long notaFiscalTipoId,
        Long vendedorId,
        Long clienteId,
        Long transportadoraId,
        Long formaPagamentoId,
        LocalDate dataVenda,
        LocalDate dataSaida,
        String horaSaida,
        Integer numeroFatura,
        String localEntrega,
        String localCobranca,
        BigDecimal valorSubtotal,
        BigDecimal taxaComissao,
        BigDecimal valorComissao,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal,
        TipoFrete tipoFrete,
        BigDecimal valorFrete,
        BigDecimal valorSeguro,
        Integer diaFixoParcela,
        String observacao,
        SituacaoVenda situacao,
        List<VendaItemResponse> itens) {
}
