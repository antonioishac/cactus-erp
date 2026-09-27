package br.com.cactusdigital.erp.loja.entity;

/**
 * {@code TEXTO}/{@code NUMERO} aceitam qualquer valor livre (validado só quanto ao
 * tipo, não contra uma lista); {@code LISTA} exige que o valor informado seja uma das
 * {@link AtributoCategoriaOpcao} cadastradas pro atributo (ex: Tamanho só aceita
 * P/M/G/GG se essas forem as opções cadastradas).
 */
public enum TipoAtributo {
    TEXTO,
    NUMERO,
    LISTA
}
