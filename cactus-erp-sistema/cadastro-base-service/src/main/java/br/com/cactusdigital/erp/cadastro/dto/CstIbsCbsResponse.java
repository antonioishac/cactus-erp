package br.com.cactusdigital.erp.cadastro.dto;

/** {@code suportado}: o ERP já emite documento com este CST (ver {@code RegrasClassificacaoIbsCbs}). */
public record CstIbsCbsResponse(
        String codigo,
        String descricao,
        boolean exigeTributacao,
        boolean reducaoAliquota,
        boolean diferimento,
        boolean monofasica,
        boolean suportado) {
}
