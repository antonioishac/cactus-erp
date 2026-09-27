package br.com.cactusdigital.erp.seguranca.dto;

import java.time.Instant;

/** Nunca expõe {@code senhaHash}. */
public record UsuarioResponse(
        Long id,
        String login,
        String nome,
        String email,
        Long colaboradorId,
        boolean ativo,
        boolean bloqueado,
        Instant dataUltimoLogin) {
}
