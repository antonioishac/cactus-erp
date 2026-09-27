package br.com.cactusdigital.erp.cadastro.dto;

import java.util.List;

public record ConfiguracaoTributariaResponse(
        Long id,
        Long grupoTributarioId,
        String grupoTributarioNome,
        Long operacaoFiscalId,
        String operacaoFiscalNome,
        List<IcmsConfiguracaoDTO> icmsPorUf,
        PisConfiguracaoDTO pis,
        CofinsConfiguracaoDTO cofins,
        IpiConfiguracaoDTO ipi) {
}
