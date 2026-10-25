package br.com.cactusdigital.erp.vendas.mapper;

import br.com.cactusdigital.erp.vendas.dto.VendaPdvDevolucaoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaPdvItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaPdvPagamentoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaPdvResponse;
import br.com.cactusdigital.erp.vendas.entity.VendaPdv;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvDevolucao;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvItem;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvPagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VendaPdvMapper {

    VendaPdvResponse toResponse(VendaPdv venda);

    VendaPdvItemResponse toResponse(VendaPdvItem item);

    VendaPdvPagamentoResponse toResponse(VendaPdvPagamento pagamento);

    @Mapping(target = "vendaPdvItemId", source = "item.id")
    @Mapping(target = "numeroItem", source = "item.numeroItem")
    VendaPdvDevolucaoResponse toResponse(VendaPdvDevolucao devolucao);
}
