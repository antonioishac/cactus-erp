package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Linha do relatório de títulos em aberto (ABERTA/PARCIAL) — mesmo formato pros dois
 * lados ({@link TipoTituloRelatorio}): {@code pessoaId} é o {@code clienteId} de uma
 * {@code ContaReceber} ou o {@code fornecedorId} de uma {@code ContaPagar}.
 * {@code diasAtraso}/{@code valorMulta}/{@code valorJuros}/{@code valorAtualizado} saem do
 * mesmo cálculo de {@code FinanceiroMapper} usado nas consultas de título — o relatório
 * nunca diverge da tela. {@code situacao} vai como texto (nome do enum) porque o template
 * Jasper só precisa exibi-la.
 */
public record TituloEmAbertoResponse(
        Long tituloId,
        Long pessoaId,
        String numeroDocumento,
        String descricao,
        LocalDate dataEmissao,
        LocalDate dataVencimento,
        String situacao,
        Integer diasAtraso,
        BigDecimal valorOriginal,
        BigDecimal valorPago,
        BigDecimal valorSaldo,
        BigDecimal valorMulta,
        BigDecimal valorJuros,
        BigDecimal valorAtualizado) {
}
