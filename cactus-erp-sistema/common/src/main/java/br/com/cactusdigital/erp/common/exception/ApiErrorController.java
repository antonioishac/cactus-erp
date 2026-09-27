package br.com.cactusdigital.erp.common.exception;

import br.com.cactusdigital.erp.common.web.CorrelationIdFilter;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Rede de segurança final: qualquer erro que chegue ao dispatch de erro do servlet
 * container sem ter passado pelo {@link GlobalExceptionHandler} (ex: exceção lançada
 * dentro de um {@code Filter} puro, fora do ciclo normal do
 * {@code DispatcherServlet} — é o caso, por exemplo, dos endpoints do OAuth2
 * Authorization Server como {@code /oauth2/token}, que não são {@code @Controller}) cai
 * aqui em vez de sair no corpo de erro default do Spring Boot (formato antigo, sem
 * {@code traceId}/{@code codigo}, inconsistente com o resto da API).
 * <p>
 * {@link GlobalExceptionHandler} continua sendo o caminho principal — este controller só
 * garante que mesmo os 5% de erro que escapam dele ainda saiam no mesmo formato RFC 7807.
 */
@RestController
public class ApiErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ApiErrorController.class);

    private final ErrorAttributes errorAttributes;

    public ApiErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<ProblemDetail> handleError(HttpServletRequest request) {
        // O forward interno do servlet container pro /error acontece DEPOIS que o
        // CorrelationIdFilter já rodou seu finally e limpou o MDC (é o caso, por
        // exemplo, de uma exceção lançada dentro de um Filter do OAuth2 Authorization
        // Server, fora do ciclo normal do DispatcherServlet) — por isso recuperamos o
        // traceId do request attribute, que sobrevive ao forward, e o devolvemos pro MDC
        // só durante este método.
        String traceId = (String) request.getAttribute(CorrelationIdFilter.REQUEST_ATTR_TRACE_ID);
        if (traceId != null) {
            MDC.put(CorrelationIdFilter.MDC_TRACE_ID, traceId);
        }
        try {
            WebRequest webRequest = new ServletWebRequest(request);
            Throwable erro = errorAttributes.getError(webRequest);
            var atributos = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

            int statusValue = (int) atributos.getOrDefault("status", 500);
            HttpStatus status = HttpStatus.resolve(statusValue) != null ? HttpStatus.resolve(statusValue) : HttpStatus.INTERNAL_SERVER_ERROR;
            ErroComumCode errorCode = paraCodigo(status);
            // request.getRequestURI() devolveria "/error" (o alvo do forward interno) —
            // o path original fica no atributo padrão do Servlet API.
            Object pathOriginal = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            String path = pathOriginal != null ? pathOriginal.toString() : request.getRequestURI();

            ProblemDetail problemDetail = ProblemDetailFactory.build(status, errorCode, errorCode.mensagemPadrao(), path);

            if (status.is5xxServerError()) {
                log.error("[{}] {} em {} -> {}", traceId, errorCode.codigo(), path,
                        erro != null ? erro.getClass().getSimpleName() + ": " + erro.getMessage() : atributos.get("message"), erro);
            } else {
                log.warn("[{}] {} em {} -> {}", traceId, errorCode.codigo(), path,
                        erro != null ? erro.getClass().getSimpleName() + ": " + erro.getMessage() : atributos.get("message"));
            }
            return ResponseEntity.status(status).body(problemDetail);
        } finally {
            if (traceId != null) {
                MDC.remove(CorrelationIdFilter.MDC_TRACE_ID);
            }
        }
    }

    private ErroComumCode paraCodigo(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> ErroComumCode.ROTA_NAO_ENCONTRADA;
            case UNAUTHORIZED -> ErroComumCode.NAO_AUTENTICADO;
            case FORBIDDEN -> ErroComumCode.ACESSO_NEGADO;
            case BAD_REQUEST -> ErroComumCode.REQUISICAO_INVALIDA;
            default -> ErroComumCode.ERRO_INTERNO;
        };
    }
}
