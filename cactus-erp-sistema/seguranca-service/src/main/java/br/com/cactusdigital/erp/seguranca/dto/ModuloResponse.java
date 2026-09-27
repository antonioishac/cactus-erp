package br.com.cactusdigital.erp.seguranca.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record ModuloResponse(Long id, String codigo, String nome, Situacao situacao) {
}
