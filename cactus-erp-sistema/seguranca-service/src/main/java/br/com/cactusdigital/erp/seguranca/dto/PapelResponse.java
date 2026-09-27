package br.com.cactusdigital.erp.seguranca.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.util.List;

public record PapelResponse(
        Long id,
        String nome,
        String descricao,
        Situacao situacao,
        List<FuncionalidadeResponse> funcionalidades) {
}
