package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoFaixaPrecoDTO;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoGrupoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoGrupoResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoImagemDTO;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoMarcaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoMarcaResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoSubgrupoResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoTipoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoTipoResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoUnidadeRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoUnidadeResponse;
import br.com.cactusdigital.erp.cadastro.entity.Produto;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoFaixaPreco;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoGrupo;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoImagem;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoMarca;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoSubgrupo;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoTipo;
import br.com.cactusdigital.erp.cadastro.entity.ProdutoUnidade;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapeamento de toda a "família" produto (unidade/marca/grupo/subgrupo/tipo/produto +
 * faixa de preço/imagem aninhados). Os lookups simples (Unidade/Marca/Grupo/Tipo) também
 * mapeiam request -> entidade direto aqui (cópia de campo plana, sem FK pra resolver);
 * Produto e Subgrupo montam a entidade no Service, porque precisam resolver as FKs
 * (produtoUnidadeId -> ProdutoUnidade, ncmId -> Ncm, etc) via repository antes.
 */
@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "produtoUnidadeId", source = "produtoUnidade.id")
    @Mapping(target = "produtoUnidadeSigla", source = "produtoUnidade.sigla")
    @Mapping(target = "produtoMarcaId", source = "produtoMarca.id")
    @Mapping(target = "produtoMarcaNome", source = "produtoMarca.nome")
    @Mapping(target = "produtoSubgrupoId", source = "produtoSubgrupo.id")
    @Mapping(target = "produtoSubgrupoNome", source = "produtoSubgrupo.nome")
    @Mapping(target = "produtoGrupoId", source = "produtoSubgrupo.produtoGrupo.id")
    @Mapping(target = "produtoGrupoNome", source = "produtoSubgrupo.produtoGrupo.nome")
    @Mapping(target = "produtoTipoId", source = "produtoTipo.id")
    @Mapping(target = "produtoTipoDescricao", source = "produtoTipo.descricao")
    @Mapping(target = "ncmId", source = "ncm.id")
    @Mapping(target = "ncmCodigo", source = "ncm.codigo")
    @Mapping(target = "cestId", source = "cest.id")
    @Mapping(target = "cestCodigo", source = "cest.codigo")
    @Mapping(target = "grupoTributarioId", source = "grupoTributario.id")
    @Mapping(target = "grupoTributarioNome", source = "grupoTributario.nome")
    ProdutoResponse toResponse(Produto produto);

    ProdutoFaixaPrecoDTO toDto(ProdutoFaixaPreco faixaPreco);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    ProdutoFaixaPreco toEntity(ProdutoFaixaPrecoDTO dto);

    ProdutoImagemDTO toDto(ProdutoImagem imagem);

    // defaultValue: ProdutoImagemDTO.ordem é opcional (Integer, sem @NotNull) mas a
    // coluna produto_imagem.ordem é NOT NULL DEFAULT 0 — sem isso o MapStruct copia o
    // null do DTO e sobrescreve o default de campo da entidade (`= 0`), estourando
    // DataIntegrityViolationException na 1ª imagem sem ordem explícita (achado real
    // testando o cadastro de Produto ao vivo, 2026-09-01).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "ordem", source = "ordem", defaultValue = "0")
    ProdutoImagem toEntity(ProdutoImagemDTO dto);

    @Mapping(target = "produtoGrupoId", source = "produtoGrupo.id")
    @Mapping(target = "produtoGrupoNome", source = "produtoGrupo.nome")
    ProdutoSubgrupoResponse toResponse(ProdutoSubgrupo produtoSubgrupo);

    ProdutoUnidadeResponse toResponse(ProdutoUnidade produtoUnidade);

    ProdutoMarcaResponse toResponse(ProdutoMarca produtoMarca);

    ProdutoGrupoResponse toResponse(ProdutoGrupo produtoGrupo);

    ProdutoTipoResponse toResponse(ProdutoTipo produtoTipo);

    @Mapping(target = "id", ignore = true)
    ProdutoUnidade toEntity(ProdutoUnidadeRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ProdutoUnidadeRequest request, @MappingTarget ProdutoUnidade entity);

    @Mapping(target = "id", ignore = true)
    ProdutoMarca toEntity(ProdutoMarcaRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ProdutoMarcaRequest request, @MappingTarget ProdutoMarca entity);

    @Mapping(target = "id", ignore = true)
    ProdutoGrupo toEntity(ProdutoGrupoRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ProdutoGrupoRequest request, @MappingTarget ProdutoGrupo entity);

    @Mapping(target = "id", ignore = true)
    ProdutoTipo toEntity(ProdutoTipoRequest request);

    @Mapping(target = "id", ignore = true)
    void atualizar(ProdutoTipoRequest request, @MappingTarget ProdutoTipo entity);
}
