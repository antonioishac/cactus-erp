package br.com.cactusdigital.erp.cadastro.dto;

/** @param quantidadeRegularizada números pulados pendentes que passaram a regularizados (0 é válido: faixa sem gap registrado). */
public record RegularizacaoNumerosFiscaisResponse(int quantidadeRegularizada) {
}
