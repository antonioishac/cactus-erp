package br.com.cactusdigital.erp.estoque.entity;

/** {@code CEGA}: o contador não vê a quantidade do sistema (força contagem real, sem
 * viés) — a API não esconde o campo, a decisão de não exibir fica pro app cliente.
 * {@code POR_SALDO}: contador já vê o saldo esperado, só confirma/corrige. */
public enum TipoInventario {
    CEGA,
    POR_SALDO
}
