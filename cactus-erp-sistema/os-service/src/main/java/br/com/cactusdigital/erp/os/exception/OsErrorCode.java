package br.com.cactusdigital.erp.os.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code os-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum OsErrorCode implements ErrorCode {

    ITEM_PRODUTO_SEM_PRODUTO_ID("produtoId é obrigatório para item do tipo PRODUTO", HttpStatus.UNPROCESSABLE_ENTITY),
    ORCAMENTO_JA_APROVADO("O orçamento desta OS já está aprovado", HttpStatus.CONFLICT);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    OsErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "OS_" + name();
    }

    @Override
    public String mensagemPadrao() {
        return mensagemPadrao;
    }

    @Override
    public HttpStatus httpStatusPadrao() {
        return httpStatusPadrao;
    }
}
