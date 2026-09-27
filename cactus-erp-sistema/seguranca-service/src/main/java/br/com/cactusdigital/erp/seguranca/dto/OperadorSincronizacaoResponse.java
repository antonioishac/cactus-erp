package br.com.cactusdigital.erp.seguranca.dto;

/**
 * Payload de composição pro {@code sincronizacao-service} (Fase 6) provisionar
 * {@code PDV_OPERADOR} — validado contra {@code BANCO_PDV.md}: lá o operador é só
 * {@code ID_COLABORADOR + LOGIN + SENHA}, o resto ({@code nome} etc.) já mora no
 * {@code COLABORADOR}, sincronizado separadamente pelo cadastro-base-service.
 * <p>
 * {@code senhaHash} já é BCrypt (mesmo {@code PasswordEncoder} do login do ERP) — o PDV
 * autentica localmente contra a própria cópia, sem precisar de round-trip ao
 * Authorization Server (ver CLAUDE.md, "Autenticação do PDV — decisão refinada").
 * {@code nivelAutorizacao} é G/S/O/E, resolvido a partir do papel de maior precedência
 * do usuário nessa empresa — ver {@code OperadorSincronizacaoService}.
 */
public record OperadorSincronizacaoResponse(
        Long usuarioId,
        Long colaboradorId,
        String login,
        String senhaHash,
        String nivelAutorizacao,
        boolean ativo,
        boolean bloqueado) {
}
