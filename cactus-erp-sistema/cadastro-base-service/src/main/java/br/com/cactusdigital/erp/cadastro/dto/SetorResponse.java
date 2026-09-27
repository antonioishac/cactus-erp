package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record SetorResponse(Long id, String nome, String descricao, Situacao situacao) {
}
