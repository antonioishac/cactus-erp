package br.com.cactusdigital.erp.cadastro.entity;

/** Como {@link TabelaPrecoProduto#getValor()}/{@link TabelaPrecoProduto#getPercentualAjuste()} se aplicam. */
public enum TipoAjustePreco {
    /** {@code valor} é o preço final nessa tabela, ignora o {@code valorVenda} do produto. */
    VALOR_FIXO,
    /** {@code percentualAjuste} soma/subtrai sobre o {@code valorVenda} do produto (ex: -10 = 10% de desconto, +20 = 20% de acréscimo). */
    PERCENTUAL_SOBRE_VENDA
}
