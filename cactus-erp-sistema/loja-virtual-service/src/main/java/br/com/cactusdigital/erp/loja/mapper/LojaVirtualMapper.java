package br.com.cactusdigital.erp.loja.mapper;

import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaOpcaoRequest;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaOpcaoResponse;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaRequest;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaResponse;
import br.com.cactusdigital.erp.loja.dto.CarrinhoResponse;
import br.com.cactusdigital.erp.loja.dto.CategoriaLojaRequest;
import br.com.cactusdigital.erp.loja.dto.CategoriaLojaResponse;
import br.com.cactusdigital.erp.loja.dto.ItemCarrinhoResponse;
import br.com.cactusdigital.erp.loja.dto.ItemPedidoResponse;
import br.com.cactusdigital.erp.loja.dto.LojaRequest;
import br.com.cactusdigital.erp.loja.dto.LojaResponse;
import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.dto.ProdutoLojaRequest;
import br.com.cactusdigital.erp.loja.dto.ProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.dto.ValorAtributoResponse;
import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.entity.AtributoCategoria;
import br.com.cactusdigital.erp.loja.entity.AtributoCategoriaOpcao;
import br.com.cactusdigital.erp.loja.entity.Carrinho;
import br.com.cactusdigital.erp.loja.entity.CategoriaLoja;
import br.com.cactusdigital.erp.loja.entity.ItemCarrinho;
import br.com.cactusdigital.erp.loja.entity.ItemPedido;
import br.com.cactusdigital.erp.loja.entity.Loja;
import br.com.cactusdigital.erp.loja.entity.Pedido;
import br.com.cactusdigital.erp.loja.entity.ProdutoLoja;
import br.com.cactusdigital.erp.loja.entity.VarianteAtributoValor;
import br.com.cactusdigital.erp.loja.entity.VarianteProdutoLoja;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LojaVirtualMapper {

    LojaResponse toResponse(Loja loja);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planoAssinatura", source = "planoAssinatura", defaultValue = "BASICO")
    Loja toEntity(LojaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "situacaoAssinatura", ignore = true)
    @Mapping(target = "dataInicioAssinatura", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "planoAssinatura", source = "planoAssinatura", defaultValue = "BASICO")
    void atualizar(LojaRequest request, @MappingTarget Loja entity);

    @Mapping(target = "lojaId", source = "lojaId")
    @Mapping(target = "categoriaPaiId", source = "categoriaPai.id")
    CategoriaLojaResponse toResponse(CategoriaLoja categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lojaId", source = "lojaId")
    @Mapping(target = "categoriaPai", ignore = true)
    CategoriaLoja toEntity(CategoriaLojaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lojaId", ignore = true)
    @Mapping(target = "categoriaPai", ignore = true)
    void atualizar(CategoriaLojaRequest request, @MappingTarget CategoriaLoja entity);

    AtributoCategoriaOpcaoResponse toResponse(AtributoCategoriaOpcao opcao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atributoCategoria", ignore = true)
    AtributoCategoriaOpcao toEntity(AtributoCategoriaOpcaoRequest request);

    @Mapping(target = "categoriaLojaId", source = "categoriaLoja.id")
    AtributoCategoriaResponse toResponse(AtributoCategoria atributo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoriaLoja", ignore = true)
    @Mapping(target = "opcoes", ignore = true)
    AtributoCategoria toEntity(AtributoCategoriaRequest request);

    @Mapping(target = "categoriaLojaId", source = "categoriaLoja.id")
    @Mapping(target = "categoriaLojaNome", source = "categoriaLoja.nome")
    ProdutoLojaResponse toResponse(ProdutoLoja produto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lojaId", source = "lojaId")
    @Mapping(target = "categoriaLoja", ignore = true)
    @Mapping(target = "variantes", ignore = true)
    ProdutoLoja toEntity(ProdutoLojaRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lojaId", ignore = true)
    @Mapping(target = "categoriaLoja", ignore = true)
    @Mapping(target = "variantes", ignore = true)
    void atualizar(ProdutoLojaRequest request, @MappingTarget ProdutoLoja entity);

    @Mapping(target = "produtoLojaId", source = "produtoLoja.id")
    VarianteProdutoLojaResponse toResponse(VarianteProdutoLoja variante);

    @Mapping(target = "atributoCategoriaId", source = "atributoCategoria.id")
    @Mapping(target = "atributoCategoriaNome", source = "atributoCategoria.nome")
    ValorAtributoResponse toResponse(VarianteAtributoValor valor);

    @Mapping(target = "varianteProdutoLojaId", source = "varianteProdutoLoja.id")
    @Mapping(target = "skuVariante", source = "varianteProdutoLoja.sku")
    @Mapping(target = "nomeProduto", source = "varianteProdutoLoja.produtoLoja.nome")
    @Mapping(target = "valorTotal", expression = "java(item.getPrecoUnitario().multiply(item.getQuantidade()))")
    ItemCarrinhoResponse toResponse(ItemCarrinho item);

    /** {@code valorTotal} não é persistido — somado a partir dos itens toda vez (carrinho é sempre pequeno, sem custo de performance real). */
    @Mapping(target = "valorTotal", expression = "java(somarItensCarrinho(carrinho))")
    CarrinhoResponse toResponse(Carrinho carrinho);

    default BigDecimal somarItensCarrinho(Carrinho carrinho) {
        return carrinho.getItens().stream()
                .map(i -> i.getPrecoUnitario().multiply(i.getQuantidade()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Mapping(target = "clienteLojaId", source = "clienteLoja.id")
    @Mapping(target = "clienteNome", source = "clienteLoja.nome")
    @Mapping(target = "clienteEmail", source = "clienteLoja.email")
    @Mapping(target = "clienteCpf", source = "clienteLoja.cpf")
    PedidoResponse toResponse(Pedido pedido);

    ItemPedidoResponse toResponse(ItemPedido item);
}
