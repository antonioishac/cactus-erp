package br.com.cactusdigital.erp.os.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record OsStatusResponse(
        Long id,
        String codigo,
        String nome,
        Situacao situacao) {
}
