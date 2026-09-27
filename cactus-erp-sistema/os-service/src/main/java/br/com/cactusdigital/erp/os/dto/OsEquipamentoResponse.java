package br.com.cactusdigital.erp.os.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;

public record OsEquipamentoResponse(
        Long id,
        String nome,
        String descricao,
        Situacao situacao) {
}
