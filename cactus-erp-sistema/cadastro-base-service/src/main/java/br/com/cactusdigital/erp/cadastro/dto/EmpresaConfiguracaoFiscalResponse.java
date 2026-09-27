package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.cadastro.entity.TipoCertificadoDigital;
import java.time.LocalDate;

public record EmpresaConfiguracaoFiscalResponse(
        Long id,
        Long empresaId,
        TipoCertificadoDigital tipoCertificado,
        String certificadoAlias,
        LocalDate certificadoValidade,
        AmbienteFiscal ambienteFiscal,
        String serieNfe,
        Long proximoNumeroNfe,
        String serieNfce,
        Long proximoNumeroNfce,
        String serieNfse,
        Long proximoNumeroNfse,
        String serieCte,
        Long proximoNumeroCte,
        String serieMdfe,
        Long proximoNumeroMdfe,
        String csc,
        String cscIdToken) {
}
