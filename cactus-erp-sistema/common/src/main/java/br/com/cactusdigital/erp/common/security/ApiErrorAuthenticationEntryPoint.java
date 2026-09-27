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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

/**
 * Traduz falha de autenticação (token JWT ausente/expirado/inválido) pro mesmo formato
 * RFC 7807 do {@link br.com.cactusdigital.erp.common.exception.GlobalExceptionHandler} —
 * necessário porque essa exceção é interceptada pelo {@code ExceptionTranslationFilter}
 * do Spring Security, **antes** da requisição chegar no {@code DispatcherServlet}, então
 * nunca passaria pelo {@code @RestControllerAdvice}.
 */
public class ApiErrorAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(ApiErrorAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public ApiErrorAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ProblemDetail problemDetail = ProblemDetailFactory.build(
                HttpStatus.UNAUTHORIZED, ErroComumCode.NAO_AUTENTICADO, ErroComumCode.NAO_AUTENTICADO.mensagemPadrao(),
                request.getRequestURI());
        log.warn("[{}] {} em {}: {}", problemDetail.getProperties().get("traceId"),
                ErroComumCode.NAO_AUTENTICADO.codigo(), request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
