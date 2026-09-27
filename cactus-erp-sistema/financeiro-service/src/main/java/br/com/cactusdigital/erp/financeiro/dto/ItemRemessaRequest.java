package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dados do sacado (pagador) informados explicitamente pelo operador na hora de gerar a
 * remessa — {@code financeiro-service} não tem cliente OAuth2 de saída pro
 * cadastro-base-service (só é chamado, nunca chama), então não resolve nome/documento/
 * endereço do cliente sozinho. Ver Javadoc de {@code RemessaBancariaItem}.
 */
public record ItemRemessaRequest(
        @NotNull Long contaReceberId,
        @NotBlank @Size(max = 20) String sacadoNumeroInscricao,
        @NotBlank @Size(max = 100) String sacadoNome,
        @Size(max = 100) String sacadoLogradouro,
        @Size(max = 50) String sacadoBairro,
        @Size(max = 10) String sacadoCep,
        @Size(max = 50) String sacadoCidade,
        @Size(max = 2) String sacadoUf) {
}
