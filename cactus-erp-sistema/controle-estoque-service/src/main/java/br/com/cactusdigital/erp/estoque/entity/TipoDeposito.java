package br.com.cactusdigital.erp.estoque.entity;

/** {@code TRANSITO} existe pro caso raro de precisar de um depósito físico dedicado a
 * mercadoria em trânsito — o fluxo normal de {@link TransferenciaEstoque} não depende
 * disso, a quantidade em trânsito já mora na própria transferência. */
public enum TipoDeposito {
    PRINCIPAL,
    FILIAL,
    CD,
    TRANSITO
}
