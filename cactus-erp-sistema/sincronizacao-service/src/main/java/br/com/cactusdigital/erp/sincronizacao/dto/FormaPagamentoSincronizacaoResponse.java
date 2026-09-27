package br.com.cactusdigital.erp.sincronizacao.dto;

import java.time.Instant;

/** Fecha o gap que o próprio DDL do PDV aponta: "PDV_TIPO_PAGAMENTO — dados devem ser importados da retaguarda". */
public record FormaPagamentoSincronizacaoResponse(
        Long id,
        String codigo,
        String descricao,
        String icone,
        boolean tef,
        boolean imprimeVinculado,
        boolean permiteTroco,
        String tefTipoGp,
        boolean geraParcelas,
        String codigoPagamentoNfce,
        String situacao,
        Instant dataAtualizacao) {
}
