package br.com.cactusdigital.erp.common.security;

import br.com.cactusdigital.erp.common.exception.ErroComumCode;
import br.com.cactusdigital.erp.common.exception.ProblemDetailFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

/**
 * Traduz autorização negada (usuário autenticado, mas sem a authority necessária) pro
 * mesmo formato RFC 7807 do
 * {@link br.com.cactusdigital.erp.common.exception.GlobalExceptionHandler} — mesmo
 * motivo do {@link ApiErrorAuthenticationEntryPoint}: o
 * {@code ExceptionTranslationFilter} intercepta antes do {@code DispatcherServlet}.
 */
public class ApiErrorAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiErrorAccessDeniedHandler.class);

    private final ObjectMapper objectMapper;

    public ApiErrorAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        ProblemDetail problemDetail = ProblemDetailFactory.build(
                HttpStatus.FORBIDDEN, ErroComumCode.ACESSO_NEGADO, ErroComumCode.ACESSO_NEGADO.mensagemPadrao(),
                request.getRequestURI());
        log.warn("[{}] {} em {}: {}", problemDetail.getProperties().get("traceId"),
                ErroComumCode.ACESSO_NEGADO.codigo(), request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
