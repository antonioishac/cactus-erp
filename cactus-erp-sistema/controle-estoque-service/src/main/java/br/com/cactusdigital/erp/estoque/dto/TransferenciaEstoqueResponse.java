package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.SituacaoTransferencia;
import java.math.BigDecimal;
import java.time.Instant;

public record TransferenciaEstoqueResponse(
        Long id,
        Long produtoId,
        Long depositoOrigemId,
        String depositoOrigemNome,
        Long depositoDestinoId,
        String depositoDestinoNome,
        Long loteId,
        BigDecimal quantidade,
        BigDecimal custoUnitario,
        SituacaoTransferencia situacao,
        Long usuarioEnvioId,
        Instant dataEnvio,
        Long usuarioRecebimentoId,
        Instant dataRecebimento,
        String observacao) {
}
