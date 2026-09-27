package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.SituacaoNumeroSerie;

public record NumeroSerieResponse(
        Long id,
        Long produtoId,
        String numeroSerie,
        Long depositoId,
        String depositoNome,
        SituacaoNumeroSerie situacao) {
}
