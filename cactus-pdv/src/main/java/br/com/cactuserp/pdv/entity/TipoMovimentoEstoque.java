package br.com.cactuserp.pdv.entity;

/**
 * Tipo de movimentação registrada em {@link EstoqueMovimento}.
 *
 * <p>Define o sinal do efeito sobre {@code PRODUTO.QUANTIDADE_ESTOQUE}:
 * entrada/devolução somam, saída/perda subtraem, ajuste pode ir em
 * qualquer direção (a diferença é calculada a partir da quantidade
 * informada, não de um sinal fixo).
 */
public enum TipoMovimentoEstoque {
    E, // Entrada (compra, recebimento de fornecedor)
    S, // Saída (baixa por venda)
    A, // Ajuste (correção manual de saldo, inventário)
    P, // Perda (quebra, vencimento, avaria)
    D  // Devolução (produto volta ao estoque)
}
