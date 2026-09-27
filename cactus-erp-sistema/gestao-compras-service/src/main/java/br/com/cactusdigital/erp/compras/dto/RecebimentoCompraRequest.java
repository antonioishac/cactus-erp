package br.com.cactusdigital.erp.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/** {@code usuarioId} não vem daqui — extraído do JWT autenticado no controller, mesma
 * disciplina do {@code EstoqueMovimentoRequest} no controle-estoque-service. */
public record RecebimentoCompraRequest(
        @NotNull Long pedidoId,
        @NotNull LocalDate dataRecebimento,
        @Size(max = 20) String numeroNotaFiscal,
        @Size(max = 44) String chaveAcessoNfe,
        @Size(max = 250) String observacao,
        @NotEmpty @Valid List<RecebimentoCompraItemRequest> itens) {
}
