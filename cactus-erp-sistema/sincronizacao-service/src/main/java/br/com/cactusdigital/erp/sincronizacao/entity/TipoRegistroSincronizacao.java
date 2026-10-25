package br.com.cactusdigital.erp.sincronizacao.entity;

/**
 * Tipo de registro que o PDV empurra num envelope de lote ({@code POST /sync/lotes}).
 * {@link #ESTOQUE_MOVIMENTO} vai pro {@code controle-estoque-service}; os 5 de caixa/fiado
 * (Fase 6-B, 2026-10-24) vão pro {@code financeiro-service}. Venda, total por forma de
 * pagamento e devolução do PDV ainda não têm destino no ERP (ver FASE6_SINCRONIZACAO_PDV.md,
 * seção 2.3-bis) — entram aqui quando tiverem, não antes.
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
    FIADO_LANCAMENTO;

    /** Os tipos cujo destino é o {@code financeiro-service} (mesma fila, despachados lá pelo tipo). */
    public boolean isFinanceiro() {
        return this != ESTOQUE_MOVIMENTO;
    }
}
