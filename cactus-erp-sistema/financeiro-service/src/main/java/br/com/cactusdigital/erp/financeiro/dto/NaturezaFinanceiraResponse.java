package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoNaturezaFinanceira;

public record NaturezaFinanceiraResponse(
        Long id, Long empresaId, String codigo, String descricao, TipoNaturezaFinanceira tipo, boolean ativo) {
}
