package br.com.cactusdigital.erp.common.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Tradução central de exceções para o padrão RFC 7807 ({@link ProblemDetail}) usado por
 * toda API do Cactus ERP. Toda API herda este comportamento (a classe é escaneada junto
 * com o restante de {@code br.com.cactusdigital.erp.common} pelos serviços).
 * <p>
 * Estende {@link ResponseEntityExceptionHandler} (padrão recomendado pelo próprio Spring
 * desde a versão 6) pra ganhar de graça a tradução correta de erros de framework (tipo de
 * parâmetro errado, método HTTP não suportado, JSON malformado, rota inexistente, etc. —
 * hoje caindo todos no 500 genérico) sem reimplementar handler pra cada um; só
 * sobrescrevemos os pontos onde queremos uma mensagem amigável em português.
 * <p>
 * Todo caminho — os handlers próprios abaixo e os herdados do Spring — converge pro
 * {@link #handleExceptionInternal}, que é o único lugar que decide o nível de log
 * (WARN pra erro esperado de cliente, ERROR com stack trace completa só pra 5xx) e
 * garante {@code traceId}/{@code timestamp} em toda resposta. É esse {@code traceId}
 * (mesmo do log estruturado) que liga a mensagem amigável que o usuário vê à stack trace
 * que o dev vê — nunca expomos detalhe interno na resposta HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---- exceções de domínio do Cactus ERP -------------------------------------------

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(
                HttpStatus.NOT_FOUND, ErroComumCode.RECURSO_NAO_ENCONTRADO, ex.getMessage(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusiness(BusinessException ex, WebRequest request) {
        HttpStatus status = ex.getErrorCode().httpStatusPadrao();
        ProblemDetail problemDetail = ProblemDetailFactory.build(status, ex.getErrorCode(), ex.getMessage(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflito(ConflictException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // ---- validação fora do @Valid em @RequestBody (ex: @Validated em @PathVariable/@RequestParam) --

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<ProblemDetailFactory.CampoInvalido> campos = ex.getConstraintViolations().stream()
                .map(v -> new ProblemDetailFactory.CampoInvalido(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        ProblemDetail problemDetail = ProblemDetailFactory.buildValidacao(request, campos);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // ---- infraestrutura (banco, segurança) ---------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleIntegridade(DataIntegrityViolationException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.CONFLICT,
                ErroComumCode.CONFLITO_INTEGRIDADE, ErroComumCode.CONFLITO_INTEGRIDADE.mensagemPadrao(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Cobre {@code ?sort=} inválido em qualquer endpoint paginado ({@code Pageable}) —
     * ex: nome de campo que não existe na entidade, ou o placeholder default do Swagger
     * UI ({@code ["string"]}) enviado sem edição. {@link PropertyReferenceException} é o
     * Spring Data reclamando de um nome de propriedade que não existe;
     * {@link InvalidDataAccessApiUsageException} é o que {@code QueryUtils} lança quando
     * a expressão de sort nem chega a parecer um nome de propriedade (caracteres
     * inválidos, como colchetes/aspas). Os dois são erro de entrada do cliente — nunca
     * deveriam ter saído como 500 genérico.
     */
    @ExceptionHandler({PropertyReferenceException.class, InvalidDataAccessApiUsageException.class})
    public ResponseEntity<Object> handleParametroConsultaInvalido(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.BAD_REQUEST, ErroComumCode.PARAMETRO_INVALIDO,
                "Parâmetro de ordenação ou consulta inválido — confira os nomes de campo usados em 'sort'", request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Cobre {@link AuthenticationException} lançada manualmente dentro de um
     * {@code @RestController} (ex: {@code BadCredentialsException} de um endpoint de
     * login que valida credenciais na mão, como o {@code DevAuthController}) — diferente
     * da falha de autenticação padrão do fluxo OAuth2 (token ausente/expirado/inválido),
     * que é interceptada antes daqui pelo {@code ExceptionTranslationFilter} (ver
     * {@link br.com.cactusdigital.erp.common.security.ApiErrorAuthenticationEntryPoint}).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAutenticacao(AuthenticationException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.UNAUTHORIZED,
                ErroComumCode.NAO_AUTENTICADO, ex.getMessage(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Defesa extra: na prática, {@link AccessDeniedException} lançada pelo
     * {@code @PreAuthorize} normalmente é interceptada pelo
     * {@code ExceptionTranslationFilter} do Spring Security antes de chegar aqui (ver
     * {@link br.com.cactusdigital.erp.common.security.ApiErrorAccessDeniedHandler}, que é
     * quem trata o caso comum) — mas mantemos este handler como rede de segurança pra
     * qualquer caminho não padrão que ainda propague a exceção até o controller advice.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAcessoNegado(AccessDeniedException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.FORBIDDEN,
                ErroComumCode.ACESSO_NEGADO, ErroComumCode.ACESSO_NEGADO.mensagemPadrao(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenerica(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.INTERNAL_SERVER_ERROR,
                ErroComumCode.ERRO_INTERNO, ErroComumCode.ERRO_INTERNO.mensagemPadrao(), request);
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ---- overrides do ResponseEntityExceptionHandler: só pra deixar a mensagem amigável em pt-BR ----

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ProblemDetailFactory.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ProblemDetailFactory.CampoInvalido(erro.getField(), erro.getDefaultMessage()))
                .toList();
        ProblemDetail problemDetail = ProblemDetailFactory.buildValidacao(request, campos);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.BAD_REQUEST,
                ErroComumCode.CORPO_REQUISICAO_INVALIDO, ErroComumCode.CORPO_REQUISICAO_INVALIDO.mensagemPadrao(), request);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String mensagem = "O parâmetro obrigatório '" + ex.getParameterName() + "' não foi informado";
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.BAD_REQUEST, ErroComumCode.PARAMETRO_INVALIDO, mensagem, request);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String mensagem = "O método " + ex.getMethod() + " não é suportado por esta rota";
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.METHOD_NOT_ALLOWED, ErroComumCode.METODO_NAO_SUPORTADO, mensagem, request);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.NOT_FOUND,
                ErroComumCode.ROTA_NAO_ENCONTRADA, ErroComumCode.ROTA_NAO_ENCONTRADA.mensagemPadrao(), request);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String mensagem = "O parâmetro '" + ex.getPropertyName() + "' tem um valor com tipo inválido";
        ProblemDetail problemDetail = ProblemDetailFactory.build(HttpStatus.BAD_REQUEST, ErroComumCode.PARAMETRO_INVALIDO, mensagem, request);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    // ---- funil único: traceId/timestamp garantidos + log no nível certo -----------------

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail problemDetail = (body instanceof ProblemDetail pd)
                ? pd
                : ProblemDetailFactory.build(HttpStatus.valueOf(statusCode.value()), ErroComumCode.ERRO_INTERNO,
                        ErroComumCode.ERRO_INTERNO.mensagemPadrao(), request);
        ProblemDetailFactory.garantirRastreio(problemDetail);

        Object traceId = problemDetail.getProperties().get("traceId");
        Object codigo = problemDetail.getProperties().get("codigo");
        String path = request.getDescription(false);
        if (statusCode.is5xxServerError()) {
            log.error("[{}] {} em {} -> {}: {}", traceId, codigo, path, ex.getClass().getSimpleName(), ex.getMessage(), ex);
        } else {
            log.warn("[{}] {} em {} -> {}: {}", traceId, codigo, path, ex.getClass().getSimpleName(), ex.getMessage());
        }
        return super.handleExceptionInternal(ex, problemDetail, headers, statusCode, request);
    }
}
