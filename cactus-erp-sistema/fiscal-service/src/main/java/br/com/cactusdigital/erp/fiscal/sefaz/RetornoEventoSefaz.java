package br.com.cactusdigital.erp.fiscal.sefaz;

import java.time.Instant;

/**
 * Resultado interpretado de um evento/pedido à SEFAZ que não é a autorização (cancelamento, inutilização).
 *
 * @param homologado a SEFAZ registrou o pedido (cancelamento 135/155, inutilização 102).
 * @param xml documento a guardar: {@code procEventoNFe} no cancelamento, {@code retInutNFe} na inutilização. Também
 *        presente na rejeição, quando a SEFAZ devolveu um retorno.
 */
public record RetornoEventoSefaz(
        boolean homologado,
        String codigoStatus,
        String motivo,
        String protocolo,
        Instant dataRegistro,
        String xml) {
}
