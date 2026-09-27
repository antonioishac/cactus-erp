package br.com.cactusdigital.erp.seguranca.dto;

/** Listagem administrativa — nunca inclui o secret (só existe em texto puro na criação). */
public record ClienteLojaResumoResponse(String clientId, Long empresaId, String descricao) {
}
