package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.cadastro.entity.TipoCertificadoDigital;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** {@code certificadoAlias} referencia uma entrada de keystore/secret manager — a senha do certificado nunca trafega aqui. */
public record EmpresaConfiguracaoFiscalRequest(
        TipoCertificadoDigital tipoCertificado,
        @Size(max = 100) String certificadoAlias,
        LocalDate certificadoValidade,
        @NotNull AmbienteFiscal ambienteFiscal,
        @Size(max = 3) String serieNfe,
        Long proximoNumeroNfe,
        @Size(max = 3) String serieNfce,
        Long proximoNumeroNfce,
        @Size(max = 3) String serieNfse,
        Long proximoNumeroNfse,
        @Size(max = 3) String serieCte,
        Long proximoNumeroCte,
        @Size(max = 3) String serieMdfe,
        Long proximoNumeroMdfe,
        @Size(max = 100) String csc,
        @Size(max = 10) String cscIdToken) {
}
