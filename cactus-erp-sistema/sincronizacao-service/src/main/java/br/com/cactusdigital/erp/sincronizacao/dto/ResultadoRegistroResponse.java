package br.com.cactusdigital.erp.sincronizacao.dto;

/**
 * {@code situacao=ACEITO} = passou validação de envelope e foi persistido/publicado —
 * não significa que o consumidor de domínio já aplicou, só que não será perdido.
 * {@code situacao=REJEITADO} = erro de forma (payload malformado), o PDV precisa
 * corrigir e reenviar — não entra na fila, não gera {@code RegistroSincronizacao}.
 */
public record ResultadoRegistroResponse(String idOrigem, String chaveIdempotencia, String situacao, String motivo) {

    public static ResultadoRegistroResponse aceito(String idOrigem, String chaveIdempotencia) {
        return new ResultadoRegistroResponse(idOrigem, chaveIdempotencia, "ACEITO", null);
    }

    public static ResultadoRegistroResponse rejeitado(String idOrigem, String motivo) {
        return new ResultadoRegistroResponse(idOrigem, null, "REJEITADO", motivo);
    }
}
