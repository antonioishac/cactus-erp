package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.crm.entity.TipoEncerramentoFunil;

public record EstagioFunilResponse(
        Long id,
        String codigo,
        String nome,
        Integer ordem,
        TipoEncerramentoFunil tipoEncerramento,
        Situacao situacao) {
}
