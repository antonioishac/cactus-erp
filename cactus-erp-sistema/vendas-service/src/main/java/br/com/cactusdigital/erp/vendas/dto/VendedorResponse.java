package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;
import java.time.Instant;

public record VendedorResponse(
        Long id,
        Long colaboradorId,
        Long comissaoPerfilId,
        String comissaoPerfilNome,
        BigDecimal comissaoPercentual,
        BigDecimal metaVenda,
        Situacao situacao,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
