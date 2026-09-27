package br.com.cactusdigital.erp.compras.dto;

import br.com.cactusdigital.erp.compras.entity.TipoRequisicaoCompra;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record RequisicaoCompraRequest(
        @NotNull Long colaboradorId,
        @NotNull TipoRequisicaoCompra tipo,
        @Size(max = 100) String descricao,
        LocalDate dataRequisicao,
        @Size(max = 250) String observacao,
        @NotEmpty @Valid List<RequisicaoCompraItemRequest> itens) {
}
