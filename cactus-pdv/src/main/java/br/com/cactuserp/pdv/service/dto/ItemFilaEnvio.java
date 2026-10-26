package br.com.cactuserp.pdv.service.dto;

/** Uma linha da fila de envio ao ERP ({@code SINCRONIZACAO_ENVIO}) para a tela de sincronização. */
public record ItemFilaEnvio(long id, String tipoRegistro, String idOrigem, String status, int tentativas,
                            String ultimoErro, String dataCriacao, String dataEnvio) {

    /** Nome do tipo de registro para o operador (o técnico fica na coluna de id de origem). */
    public String tipoDescricao() {
        return switch (tipoRegistro) {
            case "VENDA_PDV" -> "Venda";
            case "DEVOLUCAO_PDV" -> "Devolução";
            case "CANCELAMENTO_VENDA_PDV" -> "Cancelamento de venda";
            case "ABERTURA_CAIXA" -> "Abertura de caixa";
            case "SUPRIMENTO" -> "Suprimento";
            case "SANGRIA" -> "Sangria";
            case "CONFERENCIA_CAIXA" -> "Conferência de caixa";
            case "FIADO_LANCAMENTO" -> "Fiado";
            case "ESTOQUE_MOVIMENTO" -> "Movimento de estoque";
            default -> tipoRegistro;
        };
    }

    /** Rótulo do status para a tela. */
    public String statusDescricao() {
        return switch (status) {
            case "P" -> "Pendente";
            case "E" -> "Enviado";
            case "R" -> "Rejeitado";
            case "B" -> "Bloqueado";
            case "D" -> "Dispensado";
            default -> status;
        };
    }
}
