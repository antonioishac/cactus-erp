package br.com.cactusdigital.erp.sincronizacao.entity;

/**
 * Tipo de registro que o PDV empurra num envelope de lote ({@code POST /sync/lotes}).
 * {@link #ESTOQUE_MOVIMENTO} vai pro {@code controle-estoque-service}; os 5 de caixa/fiado
 * (Fase 6-B, 2026-10-24) vão pro {@code financeiro-service}; venda e devolução do PDV vão pro
 * {@code vendas-service} (ver FASE6_SINCRONIZACAO_PDV.md, seção 12).
 */
public enum TipoRegistroSincronizacao {
    ESTOQUE_MOVIMENTO,
    /** {@code PDV_SANGRIA} — saída de dinheiro do caixa do terminal. */
    SANGRIA,
    /** {@code PDV_SUPRIMENTO} — entrada de dinheiro no caixa do terminal durante o turno. */
    SUPRIMENTO,
    /** {@code PDV_MOVIMENTO} na abertura — o fundo de troco inicial entra no caixa do terminal. */
    ABERTURA_CAIXA,
    /** {@code PDV_CONFERENCIA_CAIXA} — a divergência contado×esperado vira ajuste no caixa do terminal. */
    CONFERENCIA_CAIXA,
    /** {@code CLIENTE_FIADO_LANCAMENTO} — C=compra a prazo (título a receber), P=pagamento (baixa). */
    FIADO_LANCAMENTO,
    /** {@code PDV_VENDA_CABECALHO} + itens + {@code PDV_TOTAL_TIPO_PAGAMENTO} (pagamento fracionado) — vendas-service. */
    VENDA_PDV,
    /** {@code PDV_DEVOLUCAO} — devolução pós-venda de um item, vendas-service. */
    DEVOLUCAO_PDV,
    /** Cancelamento de uma venda do PDV JÁ FECHADA (e já enviada), com NFC-e cancelada quando havia — vendas-service.
     * Tipo próprio porque reenviar a venda com o mesmo {@code idOrigem} seria descartado pela deduplicação. */
    CANCELAMENTO_VENDA_PDV;

    /** Os tipos cujo destino é o {@code financeiro-service} (mesma fila, despachados lá pelo tipo). */
    public boolean isFinanceiro() {
        return this != ESTOQUE_MOVIMENTO && !isVendas();
    }

    /** Os tipos cujo destino é o {@code vendas-service} (mesma fila, despachados lá pelo tipo). */
    public boolean isVendas() {
        return this == VENDA_PDV || this == DEVOLUCAO_PDV || this == CANCELAMENTO_VENDA_PDV;
    }
}
