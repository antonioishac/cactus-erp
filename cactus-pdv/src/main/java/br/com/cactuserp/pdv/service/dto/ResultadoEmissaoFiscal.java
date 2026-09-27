package br.com.cactuserp.pdv.service.dto;

/**
 * Resultado de uma tentativa de emissão fiscal (ver {@code EmissaoFiscalService}).
 */
public class ResultadoEmissaoFiscal {

    public enum Status {
        /** Emitido com sucesso pelo módulo principal ou de contingência. */
        EMITIDO,
        /** ACBr Monitor não configurado nesta loja — venda segue como recibo simples. */
        NAO_CONFIGURADO,
        /** Falhou em todos os módulos disponíveis; precisa reprocessar depois. */
        PENDENTE_REPROCESSAMENTO
    }

    private final Status status;
    private final String moduloUtilizado; // NFC-E | SAT | MFE | null
    private final String mensagem;

    private ResultadoEmissaoFiscal(Status status, String moduloUtilizado, String mensagem) {
        this.status = status;
        this.moduloUtilizado = moduloUtilizado;
        this.mensagem = mensagem;
    }

    public static ResultadoEmissaoFiscal emitido(String modulo, String mensagem) {
        return new ResultadoEmissaoFiscal(Status.EMITIDO, modulo, mensagem);
    }

    public static ResultadoEmissaoFiscal naoConfigurado() {
        return new ResultadoEmissaoFiscal(Status.NAO_CONFIGURADO, null,
                "ACBr Monitor não configurado para este caixa — venda concluída como recibo simples.");
    }

    public static ResultadoEmissaoFiscal pendente(String mensagem) {
        return new ResultadoEmissaoFiscal(Status.PENDENTE_REPROCESSAMENTO, null, mensagem);
    }

    public Status getStatus() { return status; }
    public String getModuloUtilizado() { return moduloUtilizado; }
    public String getMensagem() { return mensagem; }

    public boolean foiEmitido() { return status == Status.EMITIDO; }
}
