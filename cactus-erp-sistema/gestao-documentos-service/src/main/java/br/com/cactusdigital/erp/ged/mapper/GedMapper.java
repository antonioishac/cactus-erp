package br.com.cactusdigital.erp.ged.mapper;

import br.com.cactusdigital.erp.ged.dto.DocumentoResponse;
import br.com.cactusdigital.erp.ged.dto.DocumentoVersaoResponse;
import br.com.cactusdigital.erp.ged.dto.PastaDocumentoResponse;
import br.com.cactusdigital.erp.ged.dto.TipoDocumentoResponse;
import br.com.cactusdigital.erp.ged.entity.Documento;
import br.com.cactusdigital.erp.ged.entity.DocumentoVersao;
import br.com.cactusdigital.erp.ged.entity.PastaDocumento;
import br.com.cactusdigital.erp.ged.entity.TipoDocumento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GedMapper {

    TipoDocumentoResponse toResponse(TipoDocumento tipoDocumento);

    @Mapping(target = "pastaPaiId", source = "pastaPai.id")
    @Mapping(target = "pastaPaiNome", source = "pastaPai.nome")
    PastaDocumentoResponse toResponse(PastaDocumento pasta);

    @Mapping(target = "pastaId", source = "pasta.id")
    @Mapping(target = "pastaNome", source = "pasta.nome")
    @Mapping(target = "tipoDocumentoId", source = "tipoDocumento.id")
    @Mapping(target = "tipoDocumentoNome", source = "tipoDocumento.nome")
    DocumentoResponse toResponse(Documento documento);

    @Mapping(target = "documentoId", source = "documento.id")
    DocumentoVersaoResponse toResponse(DocumentoVersao versao);
}
