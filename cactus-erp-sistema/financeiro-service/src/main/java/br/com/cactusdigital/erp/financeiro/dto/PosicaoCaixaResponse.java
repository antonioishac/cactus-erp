package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.util.List;

public record PosicaoCaixaResponse(BigDecimal saldoGeral, List<PosicaoCaixaTipoResponse> porTipo) {
}
