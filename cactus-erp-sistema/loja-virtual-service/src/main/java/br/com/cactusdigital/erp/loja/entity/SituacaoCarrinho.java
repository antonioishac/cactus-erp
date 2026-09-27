package br.com.cactusdigital.erp.loja.entity;

/** {@code ABERTO} (comprando) → {@code FINALIZADO} (virou {@code Pedido}) — {@code ABANDONADO} fica reservado pra uma rotina futura de limpeza por inatividade, não usado ainda. */
public enum SituacaoCarrinho {
    ABERTO,
    FINALIZADO,
    ABANDONADO
}
