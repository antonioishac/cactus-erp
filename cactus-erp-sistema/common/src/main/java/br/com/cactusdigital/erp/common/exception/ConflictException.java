package br.com.cactusdigital.erp.common.exception;

/**
 * Conflito de estado explícito de negócio (ex: aprovar um pedido de compra que já foi
 * aprovado por outro usuário, fechar um caixa que já está fechado) — diferente de
 * {@link org.springframework.dao.DataIntegrityViolationException}, que é conflito de
 * integridade no banco (chave única/FK). Sempre traduzida para HTTP 409.
 */
public class ConflictException extends RuntimeException {

    private final ErrorCode errorCode;

    public ConflictException(ErrorCode errorCode) {
        this(errorCode, errorCode.mensagemPadrao());
    }

    public ConflictException(ErrorCode errorCode, String mensagem) {
        super(mensagem);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
