package br.com.cactusdigital.erp.seguranca.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code seguranca-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum SegurancaErrorCode implements ErrorCode {

    USUARIO_SENHA_OBRIGATORIA("Senha é obrigatória ao criar um usuário", HttpStatus.UNPROCESSABLE_ENTITY),
    USUARIO_PAPEL_EMPRESA_DUPLICADO("Usuário já possui esse papel nessa empresa", HttpStatus.CONFLICT),
    USUARIO_SEM_ACESSO_EMPRESA("Usuário não tem papel atribuído nessa empresa", HttpStatus.UNPROCESSABLE_ENTITY),
    CLIENTE_LOJA_JA_EXISTE("Já existe um client de sincronização provisionado para essa loja", HttpStatus.CONFLICT);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    SegurancaErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "SEGURANCA_" + name();
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
