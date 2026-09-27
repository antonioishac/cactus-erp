package br.com.cactusdigital.erp.cadastro.dto;

/**
 * Resultado achatado (CST/CSOSN/alíquotas já resolvidos) — é este formato que, na Fase 6
 * de sincronização, alimenta {@code TAXA_ICMS/TAXA_IPI/TAXA_PIS/TAXA_COFINS/TAXA_ISSQN},
 * {@code CST}/{@code CSOSN} do {@code PRODUTO} no cactus-pdv. {@code iss} só vem
 * preenchido quando a {@code OperacaoFiscal} é de serviço.
 */
public record ResolverTributacaoResponse(
        Long grupoTributarioId,
        String grupoTributarioNome,
        Long operacaoFiscalId,
        String operacaoFiscalNome,
        Integer cfopCodigo,
        String ufDestino,
        IcmsConfiguracaoDTO icms,
        PisConfiguracaoDTO pis,
        CofinsConfiguracaoDTO cofins,
        IpiConfiguracaoDTO ipi,
        IssConfiguracaoDTO iss) {
}
