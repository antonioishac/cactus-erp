package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoContaBancaria;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContaBancariaResponse(
        Long id,
        Long empresaId,
        Long bancoId,
        TipoContaBancaria tipo,
        String nome,
        String agencia,
        String agenciaDigito,
        String numero,
        String numeroDigito,
        String cedenteNome,
        String cedenteDocumento,
        String codigoConvenioCobranca,
        String codigoCedenteCobranca,
        String carteiraCobranca,
        Long proximoNossoNumero,
        LocalDate dataSaldoInicial,
        BigDecimal saldoInicial,
        BigDecimal saldoAtual,
        boolean ativo) {
}
