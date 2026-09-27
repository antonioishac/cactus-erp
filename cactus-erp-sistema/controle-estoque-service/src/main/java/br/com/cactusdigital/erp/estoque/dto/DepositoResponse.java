package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.estoque.entity.TipoDeposito;

public record DepositoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
        TipoDeposito tipo,
        Situacao situacao) {
}
