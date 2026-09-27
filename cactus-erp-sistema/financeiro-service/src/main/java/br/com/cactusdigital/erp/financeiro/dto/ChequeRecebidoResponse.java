package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.SituacaoChequeRecebido;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ChequeRecebidoResponse(
        Long id,
        Long clienteId,
        String cpfCnpj,
        String nomeEmitente,
        String bancoCodigo,
        String agencia,
        String conta,
        String numero,
        LocalDate dataEmissao,
        LocalDate bomPara,
        BigDecimal valor,
        SituacaoChequeRecebido situacao,
        LocalDate dataSituacao,
        String motivoSituacao,
        Long contaBancariaId) {
}
