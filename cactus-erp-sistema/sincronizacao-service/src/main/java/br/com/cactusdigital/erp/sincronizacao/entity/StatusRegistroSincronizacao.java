package br.com.cactusdigital.erp.sincronizacao.entity;

/**
 * Ciclo de vida de um {@link RegistroSincronizacao} — ver
 * FASE6_SINCRONIZACAO_PDV.md, seção 6.5.
 * <pre>
 * RECEBIDO (persistido, aguardando publicação)
 *   → PUBLICADO (na fila, outbox concluído)
 *     → PROCESSADO (consumidor de domínio aplicou com sucesso)
 *     → ERRO (esgotou tentativas, foi pra DLQ final — ação manual necessária)
 * </pre>
 */
public enum StatusRegistroSincronizacao {
    RECEBIDO,
    PUBLICADO,
    PROCESSADO,
    ERRO
}
