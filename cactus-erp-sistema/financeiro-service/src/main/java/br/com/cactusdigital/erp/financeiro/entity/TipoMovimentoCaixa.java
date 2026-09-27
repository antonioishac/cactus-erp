package br.com.cactusdigital.erp.financeiro.entity;

/**
 * Só direção do lançamento no ledger — sempre em conjunto com um valor positivo
 * ({@link MovimentoCaixaBanco#getValor()} nunca é negativo, o tipo é que decide se soma
 * ou subtrai do saldo). Deliberadamente sem um valor "AJUSTE" aqui (que exigiria decidir
 * o sinal por fora) — ajuste manual usa {@code ENTRADA}/{@code SAIDA} normalmente,
 * categorizado como tal só via {@code OrigemMovimentoCaixa#AJUSTE}. Mesmo critério de
 * {@code EstoqueMovimento} (tipo decide direção, quantidade sempre positiva).
 */
public enum TipoMovimentoCaixa {
    ENTRADA,
    SAIDA,
    TRANSFERENCIA_ENTRADA,
    TRANSFERENCIA_SAIDA
}
