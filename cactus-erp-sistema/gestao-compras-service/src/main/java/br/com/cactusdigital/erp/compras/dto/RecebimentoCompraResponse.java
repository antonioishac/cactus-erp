package br.com.cactusdigital.erp.compras.dto;

import java.time.LocalDate;
import java.util.List;

public record RecebimentoCompraResponse(
        Long id,
        Long pedidoId,
        Long usuarioId,
        LocalDate dataRecebimento,
        String numeroNotaFiscal,
        String chaveAcessoNfe,
        String observacao,
        List<RecebimentoCompraItemResponse> itens) {
}
