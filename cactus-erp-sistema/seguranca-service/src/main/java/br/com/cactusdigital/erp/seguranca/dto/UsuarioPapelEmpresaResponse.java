package br.com.cactusdigital.erp.seguranca.dto;

/**
 * {@code empresaNome} propositalmente ausente — este serviço não tem cópia local de
 * {@code Empresa} (mora no cadastro-base-service, outro schema). Quem precisa exibir o
 * nome resolve a partir do {@code empresaId} no serviço que tem esse dado.
 */
public record UsuarioPapelEmpresaResponse(
        Long id,
        Long papelId,
        String papelNome,
        Long empresaId) {
}
