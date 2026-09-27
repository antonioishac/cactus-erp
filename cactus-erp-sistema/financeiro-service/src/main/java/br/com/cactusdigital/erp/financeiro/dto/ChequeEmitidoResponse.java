package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.SituacaoChequeEmitido;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ChequeEmitidoResponse(
        Long id,
        Long contaBancariaId,
        String numero,
        LocalDate dataEmissao,
        LocalDate bomPara,
        BigDecimal valor,
        String nominalA,
        SituacaoChequeEmitido situacao,
        LocalDate dataSituacao,
        String motivoSituacao) {
}
