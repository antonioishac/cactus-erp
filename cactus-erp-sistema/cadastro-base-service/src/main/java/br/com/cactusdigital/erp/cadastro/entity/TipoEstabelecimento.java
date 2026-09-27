package br.com.cactusdigital.erp.cadastro.entity;

/**
 * Validado contra {@code BANCO_PDV.md.LOJA_CONFIGURACAO.TIPO_ESTABELECIMENTO} (lá é
 * varchar livre, default 'VAREJO') — o cactus-pdv é genérico (bar, restaurante,
 * supermercado, loja, autocenter, comércio em geral), por isso o enum cobre os
 * segmentos citados + OUTRO pra não travar clientes fora dessa lista.
 */
public enum TipoEstabelecimento {
    VAREJO,
    RESTAURANTE,
    BAR,
    SUPERMERCADO,
    AUTOCENTER,
    OUTRO
}
