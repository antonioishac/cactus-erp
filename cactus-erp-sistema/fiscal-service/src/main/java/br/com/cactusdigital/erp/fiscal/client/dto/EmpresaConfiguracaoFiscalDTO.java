package br.com.cactusdigital.erp.fiscal.client.dto;

/** Espelha o subconjunto do {@code EmpresaConfiguracaoFiscalResponse} (cadastro-base-service, Fase 1c) necessário pra emitir — série/numeração/ambiente. */
public record EmpresaConfiguracaoFiscalDTO(
        Long empresaId,
        String ambienteFiscal,
        String serieNfe,
        Long proximoNumeroNfe,
        String serieCte,
        Long proximoNumeroCte,
        String serieNfce,
        Long proximoNumeroNfce,
        String serieMdfe,
        Long proximoNumeroMdfe) {
}
