package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Operação fiscal e UF usadas como referência pra recalcular o "resolvido padrão" cacheado no Produto. */
public record RecalcularTributacaoPadraoRequest(
        @NotNull Long operacaoFiscalId,
        @NotBlank @Size(min = 2, max = 2) String ufDestino) {
}
