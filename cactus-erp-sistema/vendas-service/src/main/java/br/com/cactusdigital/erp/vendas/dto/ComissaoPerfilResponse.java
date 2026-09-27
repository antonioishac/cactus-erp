package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.Instant;

public record ComissaoPerfilResponse(
        Long id,
        String codigo,
        String nome,
        Situacao situacao,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
