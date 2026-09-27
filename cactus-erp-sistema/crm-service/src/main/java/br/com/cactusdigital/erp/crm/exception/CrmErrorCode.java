package br.com.cactusdigital.erp.crm.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code crm-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum CrmErrorCode implements ErrorCode {

    LEAD_JA_CONVERTIDO("Este lead já foi convertido em cliente", HttpStatus.CONFLICT),
    LEAD_SITUACAO_INVALIDA_PARA_QUALIFICAR("Só é possível qualificar um lead NOVO ou EM_QUALIFICACAO", HttpStatus.CONFLICT),
    LEAD_SITUACAO_INVALIDA_PARA_DESQUALIFICAR("Este lead já foi desqualificado ou convertido", HttpStatus.CONFLICT),
    LEAD_SITUACAO_INVALIDA_PARA_CONVERTER("Só é possível converter um lead QUALIFICADO", HttpStatus.UNPROCESSABLE_ENTITY),
    OPORTUNIDADE_JA_FECHADA("Esta oportunidade já foi ganha ou perdida — não é possível mover de estágio", HttpStatus.CONFLICT),
    MOTIVO_PERDA_OBRIGATORIO("motivoPerda é obrigatório ao mover a oportunidade para um estágio de perda", HttpStatus.UNPROCESSABLE_ENTITY),
    ATIVIDADE_SEM_VINCULO("A atividade precisa estar vinculada a um leadId e/ou oportunidadeId", HttpStatus.UNPROCESSABLE_ENTITY),
    ATIVIDADE_JA_CONCLUIDA("Esta atividade já está concluída", HttpStatus.CONFLICT),
    INTERACAO_SEM_VINCULO("A interação precisa estar vinculada a um leadId e/ou oportunidadeId", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    CrmErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "CRM_" + name();
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
