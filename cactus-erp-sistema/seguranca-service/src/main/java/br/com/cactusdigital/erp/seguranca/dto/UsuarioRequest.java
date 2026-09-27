package br.com.cactusdigital.erp.seguranca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * {@code senha} é obrigatória na criação; na atualização, {@code null} significa "não
 * alterar a senha" (troca de senha tem endpoint próprio, ver {@link AlterarSenhaRequest}).
 */
public record UsuarioRequest(
        @NotBlank @Size(max = 60) String login,
        @Size(min = 8, max = 100) String senha,
        @NotBlank @Size(max = 150) String nome,
        @Email @Size(max = 250) String email,
        Long colaboradorId,
        boolean ativo) {
}
