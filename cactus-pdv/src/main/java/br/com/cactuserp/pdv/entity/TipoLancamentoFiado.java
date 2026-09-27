package br.com.cactuserp.pdv.entity;

/**
 * Tipo de lançamento no livro-razão do fiado ({@link ClienteFiadoLancamento}).
 */
public enum TipoLancamentoFiado {
    /** Compra a prazo — aumenta o saldo devedor do cliente. */
    C,
    /** Pagamento/baixa — reduz o saldo devedor do cliente. */
    P
}
