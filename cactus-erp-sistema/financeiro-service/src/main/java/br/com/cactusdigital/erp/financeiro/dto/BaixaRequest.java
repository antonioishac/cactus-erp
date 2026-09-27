package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Compartilhado entre {@code ContaPagarController}/{@code ContaReceberController}.
 * {@code contaBancariaId} obrigatório desde 2026-09-06 (fecha o gap "Controle de Caixa e
 * Bancos" — toda baixa move dinheiro de/para uma conta real, nunca fica solta). Gera
 * automaticamente um {@code MovimentoCaixaBanco} nessa conta (ver
 * {@code MovimentoCaixaBancoService}). {@code chequeEmitidoId} só é considerado em baixa
 * de {@code ContaPagar}, {@code chequeRecebidoId} só em {@code ContaReceber} (o outro é
 * ignorado silenciosamente pelo service correspondente). {@code valorDesconto} é a
 * concessão de desconto por antecipação nesta baixa específica — só aceito se
 * {@code dataBaixa <= título.descontoAte}.
 */
public record BaixaRequest(
        LocalDate dataBaixa,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valorBaixa,
        @DecimalMin("0") BigDecimal valorDesconto,
        Long formaPagamentoId,
        @NotNull Long contaBancariaId,
        Long chequeEmitidoId,
        Long chequeRecebidoId,
        @Size(max = 250) String observacao,
        Long usuarioId) {
}
