package br.com.cactusdigital.erp.cadastro.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoPessoaConverter implements AttributeConverter<TipoPessoa, String> {

    @Override
    public String convertToDatabaseColumn(TipoPessoa attribute) {
        return attribute == null ? null : String.valueOf(attribute.getCodigo());
    }

    @Override
    public TipoPessoa convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isBlank() ? null : TipoPessoa.fromCodigo(dbData.charAt(0));
    }
}
