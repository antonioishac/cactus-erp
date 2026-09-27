package br.com.cactusdigital.erp.common.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte {@link Situacao} de/para {@code CHAR(1)} no banco ('A'/'I'), aplicado
 * automaticamente (autoApply) em toda entidade que declarar um campo {@code Situacao}.
 */
@Converter(autoApply = true)
public class SituacaoConverter implements AttributeConverter<Situacao, String> {

    @Override
    public String convertToDatabaseColumn(Situacao attribute) {
        return attribute == null ? null : String.valueOf(attribute.getCodigo());
    }

    @Override
    public Situacao convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isBlank() ? null : Situacao.fromCodigo(dbData.charAt(0));
    }
}
