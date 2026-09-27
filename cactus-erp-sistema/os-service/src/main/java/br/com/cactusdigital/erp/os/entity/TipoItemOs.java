package br.com.cactusdigital.erp.os.entity;

/** {@code fenix.sql.OS_PRODUTO_SERVICO.TIPO} (char 1) — PRODUTO referencia {@code produtoId} (peça usada), SERVICO é mão de obra (produtoId nulo, só complemento/descrição). */
public enum TipoItemOs {
    PRODUTO,
    SERVICO
}
