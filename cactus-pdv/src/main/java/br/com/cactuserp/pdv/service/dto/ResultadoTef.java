package br.com.cactuserp.pdv.service.dto;

import java.math.BigDecimal;

/** Resultado de uma transação TEF (ver {@code TefHiperlinkClient}). */
public class ResultadoTef {

    private final boolean aprovada;
    private final String nsu;
    private final String rede;
    private final String cartaoDc; // "D" ou "C"
    private final BigDecimal valor;
    private final String mensagem;

    public ResultadoTef(boolean aprovada, String nsu, String rede, String cartaoDc,
                        BigDecimal valor, String mensagem) {
        this.aprovada = aprovada;
        this.nsu = nsu;
        this.rede = rede;
        this.cartaoDc = cartaoDc;
        this.valor = valor;
        this.mensagem = mensagem;
    }

    public static ResultadoTef falha(String mensagem) {
        return new ResultadoTef(false, null, null, null, null, mensagem);
    }

    public boolean isAprovada() { return aprovada; }
    public String getNsu() { return nsu; }
    public String getRede() { return rede; }
    public String getCartaoDc() { return cartaoDc; }
    public BigDecimal getValor() { return valor; }
    public String getMensagem() { return mensagem; }
}
