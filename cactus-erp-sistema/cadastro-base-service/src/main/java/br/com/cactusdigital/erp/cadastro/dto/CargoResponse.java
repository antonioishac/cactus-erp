package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;

public record CargoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal salario,
        String cbo1994,
        String cbo2002,
        Situacao situacao) {
}
