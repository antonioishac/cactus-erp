package br.com.cactusdigital.erp.contabilidade.entity;

/**
 * Saldo normal esperado de uma conta — decide como {@link RelatorioContabilService}
 * calcula o saldo (débito−crédito ou crédito−débito) a partir das somas brutas de
 * {@link PartidaContabil}. Contas de Ativo/Despesa são {@code DEVEDORA} (saldo cresce a
 * débito); Passivo/Patrimônio Líquido/Receita são {@code CREDORA} (saldo cresce a
 * crédito) — mesma convenção contábil universal (partida dobrada), não uma decisão de
 * projeto.
 */
public enum NaturezaConta {
    DEVEDORA,
    CREDORA
}
