package br.com.cactusdigital.erp.ged.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code gestao-documentos-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum GedErrorCode implements ErrorCode {

    TIPO_DOCUMENTO_NOME_DUPLICADO("Já existe um tipo de documento com esse nome", HttpStatus.CONFLICT),
    PASTA_PAI_CIRCULAR("Uma pasta não pode ser pai de si mesma, direta ou indiretamente", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENTO_TAMANHO_EXCEDIDO("O arquivo excede o tamanho máximo permitido para esse tipo de documento", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENTO_EXCLUSAO_NAO_PERMITIDA("Este documento está marcado como não excluível", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENTO_ALTERACAO_NAO_PERMITIDA("Este documento está marcado como não alterável — não é possível enviar nova versão", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENTO_SITUACAO_INVALIDA("O documento não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    DOCUMENTO_VERSAO_SEM_CONTEUDO("Esta versão não tem arquivo associado (foi gerada por uma exclusão lógica)", HttpStatus.UNPROCESSABLE_ENTITY),
    FALHA_ARMAZENAMENTO("Falha ao gravar ou recuperar o arquivo no armazenamento", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    GedErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "GED_" + name();
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
