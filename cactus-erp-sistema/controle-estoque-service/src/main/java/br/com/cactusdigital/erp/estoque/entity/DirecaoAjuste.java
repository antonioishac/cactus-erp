package br.com.cactusdigital.erp.estoque.entity;

/** Só usado quando {@link TipoMovimentoEstoque#AJUSTE} — os demais tipos têm efeito fixo. */
public enum DirecaoAjuste {
    ACRESCIMO,
    DECRESCIMO
}
