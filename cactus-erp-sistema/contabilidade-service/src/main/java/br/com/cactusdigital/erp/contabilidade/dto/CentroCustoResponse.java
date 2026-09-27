package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record CentroCustoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        Situacao situacao) {
}
