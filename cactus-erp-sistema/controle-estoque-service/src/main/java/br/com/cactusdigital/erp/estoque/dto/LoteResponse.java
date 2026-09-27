package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.LocalDate;

public record LoteResponse(
        Long id,
        Long produtoId,
        String codigo,
        LocalDate dataFabricacao,
        LocalDate dataValidade,
        Situacao situacao) {
}
