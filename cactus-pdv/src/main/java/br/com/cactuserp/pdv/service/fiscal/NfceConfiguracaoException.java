package br.com.cactuserp.pdv.service.fiscal;

/**
 * Algo do terminal/loja impede montar ou assinar a NFC-e (certificado, cadeia, série, cadastro de produto, leiaute).
 * Nada foi enviado à SEFAZ. A venda fica {@code PENDENTE} com esta mensagem até a correção — nunca é desfeita.
 */
public class NfceConfiguracaoException extends RuntimeException {

    public NfceConfiguracaoException(String mensagem) {
        super(mensagem);
    }
}
