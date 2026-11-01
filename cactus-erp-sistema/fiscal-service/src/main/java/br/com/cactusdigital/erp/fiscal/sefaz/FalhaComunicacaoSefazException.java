package br.com.cactusdigital.erp.fiscal.sefaz;

/**
 * A NF-e foi assinada mas a transmissão não teve resposta utilizável (rede, timeout, SOAP fault). <b>Não</b> é uma
 * rejeição: a SEFAZ pode ter autorizado. Carrega a chave de acesso para a nota ficar rastreável; retransmitir a mesma nota
 * gera a mesma chave e a SEFAZ responde duplicidade (204), que {@link SefazEmissorNfe} reconcilia por consulta.
 */
public class FalhaComunicacaoSefazException extends RuntimeException {

    private final String chaveAcesso;

    public FalhaComunicacaoSefazException(String chaveAcesso, Throwable causa) {
        super("Falha de comunicação com a SEFAZ: " + causa.getMessage(), causa);
        this.chaveAcesso = chaveAcesso;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }
}
