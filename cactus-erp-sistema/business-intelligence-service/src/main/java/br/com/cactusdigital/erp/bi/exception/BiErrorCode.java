package br.com.cactusdigital.erp.bi.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code business-intelligence-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum BiErrorCode implements ErrorCode {

    FALHA_LEITURA_FONTE("Falha ao ler dados de uma fonte durante o ETL", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_ESTOQUE("Falha ao buscar curva ABC no controle-estoque-service", HttpStatus.BAD_GATEWAY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    BiErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "BI_" + name();
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
