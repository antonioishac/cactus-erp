package br.com.cactusdigital.erp.estoque.messaging;

import java.math.BigDecimal;

/**
 * Corpo da mensagem publicada pelo {@code sincronizacao-service} em
 * {@code estoque.movimento.q} (Fase 6) — cópia deliberada de
 * {@code EstoqueMovimentoRequest} mais {@code registroId}, não uma dependência Java
 * entre os dois serviços (limite de serviço: cada um com seu próprio contrato de
 * mensagem, mesmo padrão de {@code EstoqueMovimentoClientRequest} no
 * gestao-compras-service). {@code registroId} referencia o
 * {@code RegistroSincronizacao} de origem — é ele que volta na confirmação
 * (ver {@code EstoqueSincronizacaoListener}) pra fechar o ciclo de auditoria.
 * <p>
 * Sem {@code usuarioId}: todo movimento originado do PDV é serviço-a-serviço, não tem
 * JWT de usuário humano por trás — {@code EstoqueMovimento.usuarioId} fica nulo
 * (rastreabilidade continua via {@code origemTipo=VENDA_PDV}/{@code origemId}).
 */
public record EstoqueMovimentoMensagem(
        Long registroId,
        Long produtoId,
        Long depositoId,
        Long loteId,
        Long numeroSerieId,
        String tipoMovimento,
        String direcaoAjuste,
        BigDecimal quantidade,
        BigDecimal custoUnitario,
        String origemTipo,
        Long origemId,
        String observacao) {
}
