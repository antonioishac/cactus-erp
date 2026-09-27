package br.com.cactusdigital.erp.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entrada da resolução tributária: grupo do produto × operação da venda × UF do
 * destinatário — a mesma combinação usada pelo {@code fenix.sql} pra achar a célula
 * {@code TRIBUT_CONFIGURA_OF_GT} e, dentro dela, a linha de {@code TRIBUT_ICMS_UF}.
 * Recebe os ids diretamente (não {@code produtoId}) porque o {@code Produto} ainda não
 * tem {@code grupoTributarioId} — isso é trabalho da Fase 1b (rework do Produto), este
 * endpoint já fica pronto pra quando essa FK existir.
 */
public record ResolverTributacaoRequest(
        @NotNull Long grupoTributarioId,
        @NotNull Long operacaoFiscalId,
        @NotBlank @Size(min = 2, max = 2) String ufDestino) {
}
