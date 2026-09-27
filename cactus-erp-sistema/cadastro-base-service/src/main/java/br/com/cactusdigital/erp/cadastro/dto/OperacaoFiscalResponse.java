package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record OperacaoFiscalResponse(
        Long id,
        String nome,
        String descricaoNaNf,
        Long cfopId,
        Integer cfopCodigo,
        String observacao,
        Situacao situacao,
        IssConfiguracaoDTO issConfiguracao) {
}
