package br.com.cactusdigital.erp.financeiro.dto;

/**
 * Resposta de {@code ContaPagarController#estornarDeCompra}/
 * {@code ContaReceberController#estornarDeVenda} — compensação de saga (ver
 * {@code common.saga.SagaOrchestrator}). Único par de endpoints do monorepo que devolvia
 * um {@code int} cru no corpo da resposta antes desta correção — todo o resto da API
 * devolve um objeto estruturado, então isto passou a seguir a mesma convenção.
 */
public record EstornoCompensacaoResponse(int quantidadeCancelada) {
}
