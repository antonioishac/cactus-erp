package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record NotaFiscalTipoResponse(
        Long id,
        Long notaFiscalModeloId,
        String notaFiscalModeloDescricao,
        String nome,
        String descricao,
        String serie,
        String serieScan,
        Integer ultimoNumero,
        Situacao situacao) {
}
