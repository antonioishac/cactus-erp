package br.com.cactusdigital.erp.common.exception;

/**
 * Recurso não encontrado por id/código. Sempre traduzida para HTTP 404 com o código de
 * catálogo {@link ErroComumCode#RECURSO_NAO_ENCONTRADO} pelo {@link GlobalExceptionHandler}.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String recurso, Object id) {
        return new NotFoundException(recurso + " não encontrado(a) para o id " + id);
    }
}
