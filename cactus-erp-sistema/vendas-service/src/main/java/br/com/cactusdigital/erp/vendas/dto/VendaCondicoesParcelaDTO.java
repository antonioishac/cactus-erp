package br.com.cactusdigital.erp.vendas.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Usado tanto na escrita (lista inteira substituída a cada atualização) quanto na
 * leitura — mesmo padrão de {@code ProdutoFaixaPrecoDTO} no cadastro-base-service. */
public record VendaCondicoesParcelaDTO(
        @NotNull Integer parcela,
        Integer dias,
        BigDecimal taxa) {
}
