package br.com.cactusdigital.erp.cadastro.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IndicadorProprioTerceiroConverter implements AttributeConverter<IndicadorProprioTerceiro, String> {

    @Override
    public String convertToDatabaseColumn(IndicadorProprioTerceiro attribute) {
        return attribute == null ? null : String.valueOf(attribute.getCodigo());
    }

    @Override
    public IndicadorProprioTerceiro convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isBlank() ? null : IndicadorProprioTerceiro.fromCodigo(dbData.charAt(0));
    }
}
