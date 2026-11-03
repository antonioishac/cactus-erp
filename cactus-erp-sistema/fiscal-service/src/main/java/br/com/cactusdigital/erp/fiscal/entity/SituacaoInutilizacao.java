package br.com.cactusdigital.erp.fiscal.entity;

/** Resultado de um pedido de inutilização de numeração à SEFAZ. Ambos são terminais (nova tentativa = novo registro). */
public enum SituacaoInutilizacao {
    /** cStat 102: faixa inutilizada, números não podem mais ser usados. */
    HOMOLOGADA,
    REJEITADA,
    /**
     * UF que dispensa o pedido de inutilização (Ajuste SINIEF 40/2026 para NF-e, 38/2026 para NFC-e: AC, BA e SP a partir
     * de 01/11/2026). Nada é enviado à SEFAZ; o salto de numeração só fica registrado e os números pulados são
     * regularizados com essa referência.
     */
    DISPENSADA
}
