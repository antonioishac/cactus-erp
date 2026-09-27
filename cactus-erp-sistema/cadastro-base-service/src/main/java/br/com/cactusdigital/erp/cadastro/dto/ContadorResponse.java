package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record ContadorResponse(Long id, String nome, String crc, String email, String telefone, Situacao situacao) {
}
