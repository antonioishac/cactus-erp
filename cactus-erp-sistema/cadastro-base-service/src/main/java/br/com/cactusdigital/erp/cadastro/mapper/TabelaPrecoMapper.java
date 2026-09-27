package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoProdutoDTO;
import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoRequest;
import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoResponse;
import br.com.cactusdigital.erp.cadastro.entity.TabelaPreco;
import br.com.cactusdigital.erp.cadastro.entity.TabelaPrecoProduto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TabelaPrecoMapper {

    TabelaPrecoResponse toResponse(TabelaPreco entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    TabelaPreco toEntity(TabelaPrecoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    void atualizar(TabelaPrecoRequest request, @MappingTarget TabelaPreco entity);

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "produtoCodigoInterno", source = "produto.codigoInterno")
    TabelaPrecoProdutoDTO toDto(TabelaPrecoProduto entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabelaPreco", ignore = true)
    @Mapping(target = "produto", ignore = true)
    TabelaPrecoProduto toEntity(TabelaPrecoProdutoDTO dto);
}
