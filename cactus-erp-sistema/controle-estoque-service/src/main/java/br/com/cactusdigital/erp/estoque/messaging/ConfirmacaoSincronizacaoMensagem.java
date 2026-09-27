package br.com.cactusdigital.erp.estoque.messaging;

/**
 * Publicada de volta pro {@code sincronizacao-service} (fila {@code sincronizacao.confirmacoes.q},
 * routing key {@code confirmacao.estoque.movimento}) depois de consumir com sucesso —
 * fecha o ciclo do {@code RegistroSincronizacao} pra {@code PROCESSADO}. Cópia
 * deliberada do record equivalente no sincronizacao-service, mesmo motivo de
 * {@link EstoqueMovimentoMensagem}. Falha (esgotou retry) NÃO publica confirmação de
 * erro aqui — quem marca {@code ERRO} é o listener da DLQ final, dentro do próprio
 * sincronizacao-service (ver FASE6_SINCRONIZACAO_PDV.md, seção 6.3).
 */
public record ConfirmacaoSincronizacaoMensagem(Long registroId, String status) {
}
