package br.com.cactusdigital.erp.contabilidade.entity;

/**
 * De onde nasceu o {@link LancamentoContabil} — {@code MANUAL} é o único caminho
 * exercitado nesta fase (contabilização automática por evento de negócio é sub-fase 2
 * futura, registrada no BACKLOG). Os demais valores já existem desde já pra quando essa
 * automação for construída não exigir migration nova (mesmo critério de
 * {@code OrigemMovimentoEstoque}/{@code ContaPagar.origemTipo} — coluna já pensada pra
 * múltiplas origens desde o desenho, mesmo com só uma em uso).
 */
public enum OrigemLancamentoContabil {
    MANUAL,
    VENDA,
    COMPRA,
    FINANCEIRO
}
