package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.contabilidade.entity.NaturezaConta;
import br.com.cactusdigital.erp.contabilidade.entity.TipoContaContabil;
import java.math.BigDecimal;

/**
 * Uma linha do balancete — {@code saldo} já vem calculado pela natureza da conta
 * ({@code DEVEDORA}: débito−crédito | {@code CREDORA}: crédito−débito), ver
 * {@code RelatorioContabilService#calcularSaldo}.
 */
public record BalanceteItemResponse(
        Long contaId,
        String contaCodigo,
        String contaNome,
        TipoContaContabil tipo,
        NaturezaConta natureza,
        BigDecimal totalDebito,
        BigDecimal totalCredito,
        BigDecimal saldo) {
}
