package br.com.cactusdigital.erp.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Log de acesso estruturado (método, path, status, duração, traceId) de toda requisição
 * de toda API do Cactus ERP — uma linha por requisição, nível INFO pra sucesso/4xx
 * esperado e WARN pra 5xx, sempre com o mesmo {@code traceId} do
 * {@link CorrelationIdFilter} (que roda antes, por isso o {@code @Order} logo em
 * seguida). É o equivalente estruturado ao access log do Tomcat/Nginx, mas já dentro do
 * mesmo pipeline de log JSON usado pelo resto da aplicação (ver {@code cactus-logback-base.xml}).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class HttpAccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("br.com.cactusdigital.erp.access");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        // Se a exceção escapa até aqui sem ter sido traduzida em resposta por nenhum
        // filtro (ex: lançada dentro de um Filter puro do OAuth2 Authorization Server,
        // fora do ciclo do DispatcherServlet — ver ApiErrorController), o container só
        // reescreve response.getStatus() pra 500 DEPOIS que este filtro já desenrolou;
        // sem isso o log de acesso registraria erroneamente o status default (200).
        boolean escapou = true;
        try {
            filterChain.doFilter(request, response);
            escapou = false;
        } finally {
            long duracaoMs = System.currentTimeMillis() - inicio;
            int status = escapou ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : response.getStatus();
            MDC.put("httpMetodo", request.getMethod());
            MDC.put("httpStatus", String.valueOf(status));
            MDC.put("httpDuracaoMs", String.valueOf(duracaoMs));
            try {
                String mensagem = "{} {} -> {} ({}ms)";
                if (status >= 500) {
                    log.warn(mensagem, request.getMethod(), request.getRequestURI(), status, duracaoMs);
                } else {
                    log.info(mensagem, request.getMethod(), request.getRequestURI(), status, duracaoMs);
                }
            } finally {
                MDC.remove("httpMetodo");
                MDC.remove("httpStatus");
                MDC.remove("httpDuracaoMs");
            }
        }
    }
}
