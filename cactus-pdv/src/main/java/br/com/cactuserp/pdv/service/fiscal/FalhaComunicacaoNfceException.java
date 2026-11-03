package br.com.cactuserp.pdv.service.fiscal;

import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

/**
 * Sem resposta utilizável da SEFAZ. Distingue os dois casos que mudam o que o PDV pode fazer com o número:
 * <ul>
 *   <li>{@link #isPodeTerSidoRecebida()} {@code false} — a conexão nem abriu (DNS, rede, recusa, handshake TLS): a SEFAZ
 *       não recebeu nada, o mesmo número pode ir em contingência offline;</li>
 *   <li>{@code true} — enviado sem resposta (timeout de leitura, SOAP fault, resposta ilegível): a NFC-e pode ter sido
 *       autorizada. A contingência sai com OUTRO número e este é verificado depois (cancelamento por substituição ou
 *       inutilização).</li>
 * </ul>
 */
public class FalhaComunicacaoNfceException extends RuntimeException {

    private final boolean podeTerSidoRecebida;

    public FalhaComunicacaoNfceException(Throwable causa) {
        super("Sem resposta da SEFAZ: " + mensagem(causa), causa);
        this.podeTerSidoRecebida = !naoConectou(causa);
    }

    public boolean isPodeTerSidoRecebida() {
        return podeTerSidoRecebida;
    }

    /** Package-private para teste. */
    static boolean naoConectou(Throwable causa) {
        for (Throwable t = causa; t != null; t = t.getCause() == t ? null : t.getCause()) {
            if (t instanceof ConnectException || t instanceof UnknownHostException || t instanceof NoRouteToHostException
                    || t instanceof SSLHandshakeException || "ConnectTimeoutException".equals(t.getClass().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    private static String mensagem(Throwable causa) {
        String texto = causa.getMessage();
        return texto == null || texto.isBlank() ? causa.getClass().getSimpleName() : texto;
    }
}
