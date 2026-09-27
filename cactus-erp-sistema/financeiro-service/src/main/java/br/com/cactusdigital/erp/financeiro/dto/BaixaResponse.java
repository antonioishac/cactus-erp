package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BaixaResponse(
        Long id,
        LocalDate dataBaixa,
        BigDecimal valorBaixa,
        BigDecimal valorDesconto,
        Long formaPagamentoId,
        Long contaBancariaId,
        Long chequeEmitidoId,
        Long chequeRecebidoId,
        String observacao,
        Long usuarioId,
        boolean estornado,
        LocalDate dataEstorno,
        String motivoEstorno) {
}
