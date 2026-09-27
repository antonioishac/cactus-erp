package br.com.cactusdigital.erp.sincronizacao.dto;

import java.math.BigDecimal;

/** Mirror de {@code ConfiguracaoOperacionalLojaResponse} (cadastro-base-service) — mesas/
 * comandas/delivery + política comercial de desconto/acréscimo, empurrados pro terminal
 * no primeiro sync (não digitados um por um, ver PDV_CONFIGURACAO no CLAUDE.md). */
public record ConfiguracaoLojaSincronizacaoResponse(
        Long empresaId,
        boolean permiteMesas,
        boolean permiteComandas,
        boolean permiteDelivery,
        String modoComanda,
        boolean permiteMesaAvulsa,
        BigDecimal limiteDescontoItemPercentual,
        BigDecimal limiteDescontoVendaPercentual,
        BigDecimal limiteDescontoVendaValor,
        BigDecimal limiteAcrescimoItemPercentual,
        BigDecimal limiteAcrescimoVendaPercentual,
        boolean permiteDescontoAcimaLimite) {
}
