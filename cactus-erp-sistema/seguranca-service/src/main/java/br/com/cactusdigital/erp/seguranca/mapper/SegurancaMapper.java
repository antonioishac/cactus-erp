package br.com.cactusdigital.erp.seguranca.mapper;

import br.com.cactusdigital.erp.seguranca.dto.FuncionalidadeResponse;
import br.com.cactusdigital.erp.seguranca.dto.ModuloResponse;
import br.com.cactusdigital.erp.seguranca.dto.PapelResponse;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioPapelEmpresaResponse;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioResponse;
import br.com.cactusdigital.erp.seguranca.entity.Funcionalidade;
import br.com.cactusdigital.erp.seguranca.entity.Modulo;
import br.com.cactusdigital.erp.seguranca.entity.Papel;
import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import br.com.cactusdigital.erp.seguranca.entity.UsuarioPapelEmpresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SegurancaMapper {

    ModuloResponse toResponse(Modulo modulo);

    @Mapping(target = "moduloId", source = "modulo.id")
    @Mapping(target = "moduloCodigo", source = "modulo.codigo")
    FuncionalidadeResponse toResponse(Funcionalidade funcionalidade);

    PapelResponse toResponse(Papel papel);

    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "papelId", source = "papel.id")
    @Mapping(target = "papelNome", source = "papel.nome")
    UsuarioPapelEmpresaResponse toResponse(UsuarioPapelEmpresa usuarioPapelEmpresa);
}
