package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.OrigemContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * {@code vencido}/{@code diasAtraso}/{@code valorMulta}/{@code valorJuros}/
 * {@code valorAtualizado} são calculados (não persistidos) — mesmo critério de
 * {@code ContaReceberResponse}, ver Javadoc de {@code FinanceiroMapper}.
 */
public record ContaPagarResponse(
        Long id,
        Long empresaId,
        Long fornecedorId,
        OrigemContaPagar origemTipo,
        Long origemId,
        String numeroDocumento,
        String descricao,
        Long naturezaFinanceiraId,
        Long tipoDocumentoId,
        Long contaBancariaId,
        Long loteId,
        LocalDate dataEmissao,
        LocalDate dataVencimento,
        LocalDate descontoAte,
        BigDecimal percentualDesconto,
        BigDecimal percentualMultaAtraso,
        BigDecimal percentualJurosDiaAtraso,
        BigDecimal valorOriginal,
        BigDecimal valorPago,
        BigDecimal valorDesconto,
        BigDecimal valorSaldo,
        SituacaoTitulo situacao,
        boolean vencido,
        Integer diasAtraso,
        BigDecimal valorMulta,
        BigDecimal valorJuros,
        BigDecimal valorAtualizado,
        String motivoEncerramento,
        List<BaixaResponse> baixas) {
}
