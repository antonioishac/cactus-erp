package br.com.cactusdigital.erp.seguranca.dto;

import java.util.List;

/** {@code empresaNome} propositalmente ausente — ver {@link UsuarioPapelEmpresaResponse}. */
public record TrocarEmpresaResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Long empresaId,
        List<String> authorities) {
}
