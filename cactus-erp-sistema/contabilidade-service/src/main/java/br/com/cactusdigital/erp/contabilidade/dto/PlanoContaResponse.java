package br.com.cactusdigital.erp.contabilidade.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.contabilidade.entity.NaturezaConta;
import br.com.cactusdigital.erp.contabilidade.entity.TipoContaContabil;

public record PlanoContaResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        Long contaPaiId,
        String contaPaiNome,
        TipoContaContabil tipo,
        NaturezaConta natureza,
        boolean contaAnalitica,
        String codigoReferencialRfb,
        Situacao situacao) {
}
