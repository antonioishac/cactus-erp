package br.com.cactusdigital.erp.common.exception;

import br.com.cactusdigital.erp.common.web.CorrelationIdFilter;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

/**
 * Fábrica central do corpo de erro padrão de toda API do Cactus ERP: RFC 7807
 * ({@link ProblemDetail}, nativo do Spring) estendido com as propriedades que todo
 * cliente/dev precisa — {@code codigo} (do catálogo {@link ErrorCode}, estável,
 * independente do texto da mensagem), {@code traceId} (mesmo ID do log estruturado,
 * ver {@link CorrelationIdFilter}) e {@code timestamp}.
 * <p>
 * {@code detail} é sempre a mensagem amigável, seguro para exibir ao usuário final —
 * nunca a mensagem de exceção crua nem stack trace, que ficam só no log (ver
 * {@link GlobalExceptionHandler}).
 */
public final class ProblemDetailFactory {

    private ProblemDetailFactory() {
    }

    public record CampoInvalido(String campo, String mensagem) {}

    public static ProblemDetail build(HttpStatus status, ErrorCode errorCode, String mensagemAmigavel, String path) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, mensagemAmigavel);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setType(URI.create("https://erros.cactuserp.com.br/" + errorCode.codigo()));
        if (path != null) {
            problemDetail.setInstance(URI.create(path));
        }
        problemDetail.setProperty("codigo", errorCode.codigo());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("traceId", MDC.get(CorrelationIdFilter.MDC_TRACE_ID));
        return problemDetail;
    }

    public static ProblemDetail build(HttpStatus status, ErrorCode errorCode, String mensagemAmigavel, WebRequest request) {
        return build(status, errorCode, mensagemAmigavel, extrairPath(request));
    }

    public static ProblemDetail buildValidacao(WebRequest request, List<CampoInvalido> camposInvalidos) {
        ProblemDetail problemDetail = build(HttpStatus.BAD_REQUEST, ErroComumCode.REQUISICAO_INVALIDA,
                ErroComumCode.REQUISICAO_INVALIDA.mensagemPadrao(), request);
        problemDetail.setProperty("camposInvalidos", camposInvalidos);
        return problemDetail;
    }

    /** Garante que o {@code traceId}/{@code timestamp} estejam presentes mesmo em um {@link ProblemDetail}
     * já criado por outro caminho (ex: os handlers default do {@link org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler}). */
    public static ProblemDetail garantirRastreio(ProblemDetail problemDetail) {
        if (problemDetail.getProperties() == null || !problemDetail.getProperties().containsKey("traceId")) {
            problemDetail.setProperty("traceId", MDC.get(CorrelationIdFilter.MDC_TRACE_ID));
        }
        if (problemDetail.getProperties() == null || !problemDetail.getProperties().containsKey("timestamp")) {
            problemDetail.setProperty("timestamp", Instant.now());
        }
        return problemDetail;
    }

    private static String extrairPath(WebRequest request) {
        if (request instanceof org.springframework.web.context.request.ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return null;
    }
}
