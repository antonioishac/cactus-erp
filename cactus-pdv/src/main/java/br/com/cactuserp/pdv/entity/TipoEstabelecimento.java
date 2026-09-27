package br.com.cactuserp.pdv.entity;

/**
 * Tipo de estabelecimento da loja.
 *
 * <p>Configurado em {@link LojaConfiguracao#getTipoEstabelecimento()}.
 * Serve como rótulo informativo principal — as funcionalidades reais
 * são controladas pelas flags individuais (permiteMesas, permiteComandas,
 * permiteDelivery), pra dar máxima flexibilidade.
 */
public enum TipoEstabelecimento {
    VAREJO,
    RESTAURANTE,
    PADARIA,
    LANCHONETE,
    PIZZARIA,
    MISTO
}