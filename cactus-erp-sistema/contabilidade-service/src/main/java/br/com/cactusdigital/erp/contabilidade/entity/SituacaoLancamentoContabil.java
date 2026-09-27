package br.com.cactusdigital.erp.contabilidade.entity;

/**
 * Lançamento contábil é imutável depois de lançado — sem {@code PUT}/{@code DELETE},
 * mesmo critério de {@code EstoqueMovimento}/{@code DocumentoVersao}/
 * {@code BaixaContaReceber}. A única forma de desfazer é {@code ESTORNADO}: gera um
 * segundo lançamento com as partidas invertidas (ver Javadoc de
 * {@code LancamentoContabilService#estornar}), nunca apaga/edita o original.
 */
public enum SituacaoLancamentoContabil {
    LANCADO,
    ESTORNADO
}
