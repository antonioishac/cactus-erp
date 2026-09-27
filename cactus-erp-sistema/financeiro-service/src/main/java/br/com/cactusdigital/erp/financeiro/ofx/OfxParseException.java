package br.com.cactusdigital.erp.financeiro.ofx;

/** Arquivo não é um OFX válido (sem nenhum bloco {@code <STMTTRN>} reconhecível) ou um
 * campo obrigatório (TRNAMT/DTPOSTED) está ausente/malformado numa transação. */
public class OfxParseException extends RuntimeException {

    public OfxParseException(String message) {
        super(message);
    }
}
