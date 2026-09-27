package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvMovimento;

/**
 * Resultado do processo de login. Indica para qual caminho o operador deve ir.
 */
public class ResultadoLogin {

    public enum Tipo {
        /** Tem movimento ativo do MESMO operador → vai pro PDV. */
        ABRIR_PDV,
        /**
         * Forçar abertura imediata de um novo movimento.
         * <p>Hoje NÃO é mais usado pelo fluxo normal de login — substituído
         * por {@link #MODO_RESTRITO}. Mantido para casos especiais futuros
         * (ex: o gerente pode forçar abertura).
         */
        ABRIR_MOVIMENTO,
        /** Movimento aberto com OUTRO operador → confirmar troca. */
        TROCAR_OPERADOR,
        /** Movimento com status T → retomar. */
        RETOMAR_MOVIMENTO,
        /**
         * Nenhum movimento ativo neste caixa → operador entra em modo
         * restrito (só consultas). Para vender precisa abrir movimento
         * via Menu Principal (F12 → Abrir Movimento).
         */
        MODO_RESTRITO
    }

    private final Tipo tipo;
    private final PdvMovimento movimento;

    private ResultadoLogin(Tipo tipo, PdvMovimento movimento) {
        this.tipo = tipo;
        this.movimento = movimento;
    }

    public static ResultadoLogin abrirPdv(PdvMovimento m) {
        return new ResultadoLogin(Tipo.ABRIR_PDV, m);
    }
    public static ResultadoLogin abrirMovimento() {
        return new ResultadoLogin(Tipo.ABRIR_MOVIMENTO, null);
    }
    public static ResultadoLogin trocarOperador(PdvMovimento m) {
        return new ResultadoLogin(Tipo.TROCAR_OPERADOR, m);
    }
    public static ResultadoLogin retomarMovimento(PdvMovimento m) {
        return new ResultadoLogin(Tipo.RETOMAR_MOVIMENTO, m);
    }
    public static ResultadoLogin modoRestrito() {
        return new ResultadoLogin(Tipo.MODO_RESTRITO, null);
    }

    public Tipo getTipo() { return tipo; }
    public PdvMovimento getMovimento() { return movimento; }
}