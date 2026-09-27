package br.com.cactusdigital.erp.estoque.entity;

/** De onde veio o movimento — {@code origemId} (quando aplicável) aponta pro registro
 * que originou (ex: {@link TransferenciaEstoque}, {@code InventarioFisicoItem}).
 * <p>
 * {@code VENDA} (Fase 7, Módulo Comercial) é distinto de {@code VENDA_PDV}: a venda
 * confirmada no {@code vendas-service} (fora do PDV) chama este serviço diretamente
 * (mesmo padrão síncrono de {@code COMPRA}), enquanto {@code VENDA_PDV} chega pelo canal
 * de sincronização assíncrono (Fase 6, ver {@code EstoqueSincronizacaoListener}) — dois
 * caminhos de entrada diferentes para o mesmo tipo de evento de negócio.
 * <p>
 * {@code ESTORNO_VENDA} — compensação de saga (ver {@code common.saga.SagaOrchestrator}
 * em {@code vendas-service.VendaService#confirmar}): uma {@code ENTRADA} real gerada
 * quando um passo POSTERIOR ao débito de estoque falha na confirmação da venda, desfazendo
 * a {@code SAIDA}/{@code VENDA} original — ledger append-only, nunca apaga o movimento
 * que está sendo compensado.
 * <p>
 * {@code ESTORNO_COMPRA} — o espelho exato do lado de compras
 * ({@code gestao-compras-service.RecebimentoCompraService#registrar}): uma {@code SAIDA}
 * real que desfaz a {@code ENTRADA}/{@code COMPRA} original quando um passo POSTERIOR
 * (geração de contas a pagar) falha. Coluna é {@code VARCHAR(25)} sem CHECK constraint,
 * então adicionar uma constante aqui não exige migration nova. */
public enum OrigemMovimentoEstoque {
    COMPRA,
    VENDA,
    VENDA_PDV,
    AJUSTE_MANUAL,
    TRANSFERENCIA,
    INVENTARIO,
    DEVOLUCAO_PDV,
    KIT,
    ESTORNO_VENDA,
    ESTORNO_COMPRA
}
