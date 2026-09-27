package br.com.cactusdigital.erp.estoque.entity;

/**
 * {@code ENTRADA}/{@code SAIDA}/{@code AJUSTE}/{@code PERDA}/{@code DEVOLUCAO} validados
 * contra o {@code TIPO_MOVIMENTO} real do {@code ESTOQUE_MOVIMENTO} do cactus-pdv (E/S/A/
 * P/D). {@code TRANSFERENCIA_SAIDA}/{@code TRANSFERENCIA_ENTRADA} são adição do ERP pro
 * multi-depósito — o PDV não tem esse conceito (produto tem só uma loja).
 * <p>
 * Efeito no saldo é sempre o mesmo pra cada tipo, exceto {@code AJUSTE}, que depende de
 * {@link DirecaoAjuste} (correção pode somar ou subtrair).
 */
public enum TipoMovimentoEstoque {
    ENTRADA,
    SAIDA,
    AJUSTE,
    PERDA,
    DEVOLUCAO,
    TRANSFERENCIA_SAIDA,
    TRANSFERENCIA_ENTRADA
}
