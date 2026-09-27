package br.com.cactusdigital.erp.contabilidade.entity;

/**
 * Direção de uma {@link PartidaContabil} — {@link PartidaContabil#getValor()} é sempre
 * positivo (mesmo critério de {@code MovimentoCaixaBanco.valor}/{@code EstoqueMovimento}
 * no resto do monorepo), este enum decide o lado.
 */
public enum TipoPartida {
    DEBITO,
    CREDITO
}
