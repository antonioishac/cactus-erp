package br.com.cactusdigital.erp.fiscal.client.dto;

/**
 * Espelha o subconjunto do {@code EmpresaConfiguracaoFiscalResponse} (cadastro-base-service, Fase 1c) necessário pra
 * emitir — série/numeração/ambiente e qual certificado usar. {@code certificadoAlias} é só o nome: arquivo e senha do
 * certificado ficam no ambiente do fiscal-service ({@code cactus.fiscal.certificados.<alias>}), nunca no banco.
 */
public record EmpresaConfiguracaoFiscalDTO(
        Long empresaId,
        String tipoCertificado,
        String certificadoAlias,
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
