package br.com.cactusdigital.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** {@code conteudoArquivo} (o texto CNAB240 em si) não vem aqui de propósito — baixa via
 * endpoint dedicado ({@code GET .../{id}/arquivo}), pra não pesar a listagem/detalhe. */
public record RemessaBancariaResponse(
        Long id,
        Long contaBancariaId,
        Long empresaId,
        LocalDate dataGeracao,
        long numeroSequencialArquivo,
        int quantidadeTitulos,
        BigDecimal valorTotal,
        List<RemessaBancariaItemResponse> itens) {
}
