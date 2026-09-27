package br.com.cactusdigital.erp.cadastro.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IndicadorArredondamentoTruncamentoConverter implements AttributeConverter<IndicadorArredondamentoTruncamento, String> {

    @Override
    public String convertToDatabaseColumn(IndicadorArredondamentoTruncamento attribute) {
        return attribute == null ? null : String.valueOf(attribute.getCodigo());
    }

    @Override
    public IndicadorArredondamentoTruncamento convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isBlank() ? null : IndicadorArredondamentoTruncamento.fromCodigo(dbData.charAt(0));
    }
}
