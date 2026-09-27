package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Provisionamento de um client OAuth2 Client Credentials vinculado a uma loja
 * (= filial/{@code Empresa}, cross-serviço no cadastro-base-service — sem FK aqui, mesmo
 * padrão de referência solta já usado em {@code Usuario.colaboradorId}). Ver
 * FASE6_SINCRONIZACAO_PDV.md, seção 11.3 — fecha o gap de isolamento por loja no
 * {@code sincronizacao-service}: o token emitido pra esse client carrega a claim
 * {@code lojaId}, que o consumidor valida contra o {@code lojaId} de cada chamada.
 */
public record ClienteLojaRequest(@NotNull Long empresaId, @NotBlank String descricao) {
}
