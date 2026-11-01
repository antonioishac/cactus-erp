package br.com.cactusdigital.erp.fiscal.sefaz;

/**
 * Resultado fiscal de uma transmissão de NF-e, já interpretado — o serviço de domínio nunca vê classes da biblioteca.
 *
 * @param xmlProcessado {@code nfeProc} (NF-e assinada + protocolo); só em {@link Resultado#AUTORIZADA}/{@link Resultado#DENEGADA}.
 */
public record RetornoSefazNfe(
        Resultado resultado,
        String chaveAcesso,
        String protocolo,
        String codigoStatus,
        String motivo,
        String xmlProcessado) {

    public enum Resultado {
        AUTORIZADA,
        DENEGADA,
        REJEITADA
    }
}
