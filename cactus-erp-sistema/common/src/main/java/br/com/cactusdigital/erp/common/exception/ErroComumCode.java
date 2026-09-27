package br.com.cactusdigital.erp.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Catálogo de erros transversais, sem dono de módulo específico — os mesmos em toda API
 * do Cactus ERP (recurso não encontrado, requisição malformada, autenticação/autorização,
 * conflito de integridade, erro interno inesperado). Erros de regra de negócio de um
 * módulo específico usam o {@link ErrorCode} próprio dele (ex: {@code CadastroErrorCode}),
 * não este enum.
 */
public enum ErroComumCode implements ErrorCode {

    RECURSO_NAO_ENCONTRADO("O recurso solicitado não foi encontrado", HttpStatus.NOT_FOUND),
    ROTA_NAO_ENCONTRADA("A rota solicitada não existe nesta API", HttpStatus.NOT_FOUND),
    REQUISICAO_INVALIDA("Um ou mais campos da requisição são inválidos", HttpStatus.BAD_REQUEST),
    CORPO_REQUISICAO_INVALIDO("O corpo da requisição está ausente ou mal formatado", HttpStatus.BAD_REQUEST),
    PARAMETRO_INVALIDO("Um parâmetro da requisição está com o tipo ou valor inválido", HttpStatus.BAD_REQUEST),
    METODO_NAO_SUPORTADO("Este método HTTP não é suportado por esta rota", HttpStatus.METHOD_NOT_ALLOWED),
    TIPO_MIDIA_NAO_SUPORTADO("O formato enviado ou solicitado não é suportado por esta rota", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    CONFLITO_INTEGRIDADE("Operação viola uma restrição de integridade (registro duplicado ou referenciado por outro cadastro)", HttpStatus.CONFLICT),
    NAO_AUTENTICADO("Autenticação necessária para acessar este recurso", HttpStatus.UNAUTHORIZED),
    ACESSO_NEGADO("Você não tem permissão para executar esta ação", HttpStatus.FORBIDDEN),
    ERRO_INTERNO("Erro interno inesperado. Contate o suporte informando o código de rastreio.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    ErroComumCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "COMUM_" + name();
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
