package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentoCaixaBancoResponse(
        Long id,
        Long contaBancariaId,
        TipoMovimentoCaixa tipoMovimento,
        OrigemMovimentoCaixa origemTipo,
        Long origemId,
        LocalDate dataMovimento,
        String historico,
        BigDecimal valor,
        BigDecimal saldoAnterior,
        BigDecimal saldoPosterior,
        Long usuarioId) {
}
