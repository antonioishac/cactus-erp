package br.com.cactusdigital.erp.seguranca.dto;

/**
 * {@code clientSecret} só aparece aqui, na criação — nunca mais é recuperável depois
 * (só o hash bcrypt fica persistido, mesmo padrão de qualquer credencial no sistema).
 * O bridge/adaptador do PDV precisa guardar isso localmente na loja.
 */
public record ClienteLojaResponse(String clientId, String clientSecret, Long empresaId, String descricao) {
}
