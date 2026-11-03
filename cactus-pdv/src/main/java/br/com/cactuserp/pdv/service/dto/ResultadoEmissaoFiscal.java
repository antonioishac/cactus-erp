package br.com.cactuserp.pdv.service.dto;

/**
 * Resultado da tentativa de emissão da NFC-e de um cupom. A venda nunca é desfeita por causa do fiscal: qualquer
 * situação diferente de {@link Status#AUTORIZADA}/{@link Status#CONTINGENCIA} fica registrada no cupom e é tratada
 * depois (reprocessamento automático ou correção).
 */
public class ResultadoEmissaoFiscal {

    public enum Status {
        /** Autorizada pela SEFAZ: imprimir o DANFE NFC-e. */
        AUTORIZADA,
        /** Emitida em contingência offline: imprimir o DANFE com a mensagem de contingência; transmissão em até 24h. */
        CONTINGENCIA,
        /** Terminal sem NFC-e habilitada — venda segue com recibo. */
        NAO_CONFIGURADO,
        /** Falha antes de assinar (certificado, cadastro, série): o job tenta de novo depois da correção. */
        PENDENTE_REPROCESSAMENTO,
        /** A SEFAZ rejeitou: precisa corrigir o cadastro e reenviar. */
        REJEITADA,
        /** Uso denegado pela SEFAZ. */
        DENEGADA
    }

    private final Status status;
    private final String mensagem;

    private ResultadoEmissaoFiscal(Status status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public static ResultadoEmissaoFiscal autorizada(String mensagem) {
        return new ResultadoEmissaoFiscal(Status.AUTORIZADA, mensagem);
    }

    public static ResultadoEmissaoFiscal contingencia(String mensagem) {
        return new ResultadoEmissaoFiscal(Status.CONTINGENCIA, mensagem);
    }

    public static ResultadoEmissaoFiscal naoConfigurado() {
        return new ResultadoEmissaoFiscal(Status.NAO_CONFIGURADO, "NFC-e não habilitada neste terminal — venda concluída com recibo.");
    }

    public static ResultadoEmissaoFiscal pendente(String mensagem) {
        return new ResultadoEmissaoFiscal(Status.PENDENTE_REPROCESSAMENTO, mensagem);
    }

    public static ResultadoEmissaoFiscal rejeitada(String mensagem) {
        return new ResultadoEmissaoFiscal(Status.REJEITADA, mensagem);
    }

    public static ResultadoEmissaoFiscal denegada(String mensagem) {
        return new ResultadoEmissaoFiscal(Status.DENEGADA, mensagem);
    }

    public Status getStatus() { return status; }
    public String getMensagem() { return mensagem; }

    /** Há documento fiscal válido para imprimir (autorizado ou em contingência offline). */
    public boolean foiEmitido() { return status == Status.AUTORIZADA || status == Status.CONTINGENCIA; }
}
