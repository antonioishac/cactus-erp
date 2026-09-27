package br.com.cactusdigital.erp.sincronizacao.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code sincronizacao-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum SincronizacaoErrorCode implements ErrorCode {

    TIPO_REGISTRO_SEM_DESTINO("Este tipo de registro ainda não tem serviço de destino no ERP", HttpStatus.UNPROCESSABLE_ENTITY),
    REGISTRO_NAO_ESTA_EM_ERRO("Só é possível reprocessar um registro com status ERRO", HttpStatus.CONFLICT),
    FALHA_INTEGRACAO_CADASTRO_BASE("Falha ao consultar o cadastro-base-service", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_ESTOQUE("Falha ao consultar o controle-estoque-service", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_SEGURANCA("Falha ao consultar o seguranca-service", HttpStatus.BAD_GATEWAY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    SincronizacaoErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "SINCRONIZACAO_" + name();
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
