package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.SituacaoOrcamento;
import br.com.cactusdigital.erp.vendas.entity.TipoFrete;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record VendaOrcamentoResponse(
        Long id,
        Long condicoesPagamentoId,
        Long vendedorId,
        Long clienteId,
        Long transportadoraId,
        String codigo,
        LocalDate dataCadastro,
        LocalDate dataEntrega,
        LocalDate validade,
        TipoFrete tipoFrete,
        BigDecimal valorSubtotal,
        BigDecimal valorFrete,
        BigDecimal taxaComissao,
        BigDecimal valorComissao,
        BigDecimal taxaDesconto,
        BigDecimal valorDesconto,
        BigDecimal valorTotal,
        String observacao,
        SituacaoOrcamento situacao,
        List<VendaOrcamentoItemResponse> itens) {
}
