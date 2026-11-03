package br.com.cactuserp.pdv.entity;

/** {@code PDV_NFCE_NUMERO_PENDENTE.SITUACAO}. Só {@link #V} ainda tem trabalho a fazer. */
public enum SituacaoNumeroPendente {
    /** Verificar na SEFAZ se a tentativa foi autorizada (e então cancelar por substituição) ou inutilizar o número. */
    V,
    /** A tentativa tinha sido autorizada e foi cancelada por substituição (evento 110112). */
    S,
    /** Número inutilizado na SEFAZ. */
    I,
    /** UF dispensa o pedido de inutilização (Ajuste SINIEF 38/2026): só registrado. */
    D,
    /** Não resolvido automaticamente — ver mensagem, exige ação do responsável fiscal. */
    E
}
