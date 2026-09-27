package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.OrigemContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * {@code vencido}/{@code diasAtraso}/{@code valorMulta}/{@code valorJuros}/
 * {@code valorAtualizado} são calculados (não persistidos) — ver Javadoc de
 * {@code FinanceiroMapper}. Os 3 últimos usam o override do título quando presente,
 * senão caem pro default de {@code ConfiguracaoFinanceiroEmpresa}; sem nenhum dos dois,
 * vêm zerados ({@code valorAtualizado=valorSaldo}).
 */
public record ContaReceberResponse(
        Long id,
        Long empresaId,
        Long clienteId,
        OrigemContaReceber origemTipo,
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
