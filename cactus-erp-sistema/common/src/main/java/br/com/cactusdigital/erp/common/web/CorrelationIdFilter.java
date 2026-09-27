package br.com.cactusdigital.erp.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Primeiro filtro da chain de toda API do Cactus ERP: garante que toda requisição tenha
 * um ID de correlação (lido de {@code X-Correlation-Id}/{@code X-Request-Id}, ou gerado
 * se o cliente não mandar nenhum) disponível via {@link MDC} durante todo o processamento
 * — inclusive dentro da autenticação/autorização do Spring Security, que roda antes do
 * {@code DispatcherServlet}.
 * <p>
 * É esse ID que liga "o que o usuário vê" (a mensagem amigável + o ID no corpo do erro,
 * ver {@link br.com.cactusdigital.erp.common.exception.GlobalExceptionHandler}) a "o que
 * o dev vê" (a stack trace completa no log estruturado, buscável pelo mesmo ID) — sem
 * precisar expor detalhe interno nenhum na resposta HTTP.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String MDC_TRACE_ID = "traceId";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    /** Sobrevive a um forward interno do servlet container (ex: dispatch de erro pro
     * {@code /error}), diferente do MDC — é de onde {@code ApiErrorController} recupera
     * o traceId quando o erro aconteceu num Filter puro, fora do ciclo normal do
     * {@code DispatcherServlet} (o {@code finally} abaixo já limpou o MDC antes do
     * forward acontecer). */
    public static final String REQUEST_ATTR_TRACE_ID = "cactus.traceId";
    private static final String HEADER_REQUEST_ID_FALLBACK = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = primeiroNaoBranco(
                request.getHeader(HEADER_CORRELATION_ID),
                request.getHeader(HEADER_REQUEST_ID_FALLBACK));
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_TRACE_ID, traceId);
        request.setAttribute(REQUEST_ATTR_TRACE_ID, traceId);
        response.setHeader(HEADER_CORRELATION_ID, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TRACE_ID);
        }
    }

    private String primeiroNaoBranco(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }
}
