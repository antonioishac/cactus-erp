package br.com.cactusdigital.erp.common.exception;

/**
 * Violação de regra de negócio (ex: tentar inativar um produto com pedido de compra em
 * aberto). Sempre carrega um {@link ErrorCode} do catálogo do módulo que a lançou — nunca
 * uma string solta — pra o {@link GlobalExceptionHandler} conseguir devolver o mesmo
 * código de forma estável na resposta (RFC 7807) independente do texto da mensagem.
 * Status HTTP default vem de {@link ErrorCode#httpStatusPadrao()} (normalmente 422 pra
 * regra de negócio, mas o próprio código pode declarar outro quando fizer sentido).
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, errorCode.mensagemPadrao());
    }

    public BusinessException(ErrorCode errorCode, String mensagem) {
        super(mensagem);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
