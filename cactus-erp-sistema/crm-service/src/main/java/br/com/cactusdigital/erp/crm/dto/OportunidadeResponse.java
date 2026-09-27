package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.SituacaoOportunidade;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record OportunidadeResponse(
        Long id,
        Long leadId,
        Long clienteId,
        Long vendedorId,
        String nome,
        BigDecimal valorEstimado,
        BigDecimal probabilidadePercentual,
        LocalDate dataPrevisaoFechamento,
        Long estagioFunilId,
        String estagioFunilNome,
        SituacaoOportunidade situacao,
        String motivoPerda,
        Instant dataFechamento,
        String observacao) {
}
