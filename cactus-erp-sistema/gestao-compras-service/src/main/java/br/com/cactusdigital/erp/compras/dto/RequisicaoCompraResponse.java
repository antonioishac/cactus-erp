package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.SituacaoRequisicaoCompra;
import br.com.cactusdigital.erp.compras.entity.TipoRequisicaoCompra;
import java.time.LocalDate;
import java.util.List;

public record RequisicaoCompraResponse(
        Long id,
        Long colaboradorId,
        TipoRequisicaoCompra tipo,
        String descricao,
        LocalDate dataRequisicao,
        SituacaoRequisicaoCompra situacao,
        String observacao,
        List<RequisicaoCompraItemResponse> itens) {
}
