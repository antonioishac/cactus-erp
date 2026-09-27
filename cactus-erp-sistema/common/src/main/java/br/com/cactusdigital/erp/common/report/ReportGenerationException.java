package br.com.cactusdigital.erp.common.report;

/**
 * Falha ao compilar/preencher/exportar um relatório (.jrxml malformado, campo referenciado
 * no template que não existe no record de dados, etc.) — sempre um erro de infraestrutura
 * do próprio serviço (bug de template), nunca uma violação de regra de negócio do usuário;
 * por isso não carrega {@code ErrorCode} nem é tratado como {@code BusinessException} —
 * cai no handler genérico 500 do {@code GlobalExceptionHandler}.
 */
public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
