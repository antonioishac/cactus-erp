package br.com.cactusdigital.erp.sincronizacao.dto;

/**
 * Mirror do record de mesmo nome no {@code seguranca-service} — desserializado direto
 * da chamada a {@code GET /api/v1/operadores}. Ver Javadoc de lá pro porquê de cada
 * campo (composição pro {@code PDV_OPERADOR}).
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
