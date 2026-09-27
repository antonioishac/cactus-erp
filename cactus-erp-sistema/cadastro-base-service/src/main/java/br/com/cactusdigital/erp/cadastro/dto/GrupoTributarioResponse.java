package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record GrupoTributarioResponse(
        Long id,
        String nome,
        String origemMercadoria,
        String observacao,
        Situacao situacao) {
}
