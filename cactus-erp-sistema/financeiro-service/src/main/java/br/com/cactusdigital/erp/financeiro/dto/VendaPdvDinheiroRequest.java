package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dinheiro líquido de uma venda do PDV (forma dinheiro − troco) — ver
 * {@code FinanceiroPdvService#registrarDinheiroVenda}. {@code registroId} é o
 * {@code RegistroSincronizacao.id} da venda: é a chave de idempotência.
 */
public record VendaPdvDinheiroRequest(
        @NotNull Long registroId,
        @NotNull Long empresaId,
        @NotBlank @Size(max = 30) String pdvDispositivoId,
        @NotNull Long pdvCaixaId,
        @Size(max = 60) String pdvCaixaNome,
        @NotNull @DecimalMin("0") BigDecimal valor,
        @NotNull LocalDate dataVenda,
        @NotNull @Size(max = 150) String descricao) {
}
