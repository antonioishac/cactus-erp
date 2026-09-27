package br.com.cactusdigital.erp.loja.entity;

/** {@code ATIVA} → {@code INADIMPLENTE} (mensalidade atrasada, loja pode continuar visível — decisão de bloqueio automático fica pra quando a cobrança recorrente for construída) → {@code CANCELADA}. */
public enum SituacaoAssinatura {
    ATIVA,
    INADIMPLENTE,
    CANCELADA
}
