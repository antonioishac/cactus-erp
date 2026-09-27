package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoContaBancaria;
import java.math.BigDecimal;

public record PosicaoCaixaTipoResponse(TipoContaBancaria tipo, int quantidadeContas, BigDecimal saldoTotal) {
}
