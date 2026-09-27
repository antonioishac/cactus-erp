package br.com.cactusdigital.erp.seguranca.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record FuncionalidadeResponse(
        Long id,
        Long moduloId,
        String moduloCodigo,
        String codigo,
        String nome,
        Situacao situacao) {
}
