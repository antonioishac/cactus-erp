package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * A célula da matriz {@link br.com.cactusdigital.erp.cadastro.entity.GrupoTributario} ×
 * {@link br.com.cactusdigital.erp.cadastro.entity.OperacaoFiscal}. {@code pis}/{@code cofins}/
 * {@code ipi} ficam nulos quando a operação não incide o respectivo tributo.
 */
public record ConfiguracaoTributariaRequest(
        @NotNull Long grupoTributarioId,
        @NotNull Long operacaoFiscalId,
        @Valid List<IcmsConfiguracaoDTO> icmsPorUf,
        @Valid PisConfiguracaoDTO pis,
        @Valid CofinsConfiguracaoDTO cofins,
        @Valid IpiConfiguracaoDTO ipi) {
}
