package br.com.cactusdigital.erp.compras.entity;

/** Validado contra {@code fenix.sql.COMPRA_TIPO_REQUISICAO}: INTERNA = itens usados pela
 * própria empresa, EXTERNA = itens pra revenda ao consumidor, MISTA = ambos. */
public enum TipoRequisicaoCompra {
    INTERNA,
    EXTERNA,
    MISTA
}
