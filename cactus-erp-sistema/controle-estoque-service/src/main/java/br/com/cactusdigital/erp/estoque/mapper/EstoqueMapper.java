package br.com.cactusdigital.erp.estoque.mapper;

import br.com.cactusdigital.erp.estoque.dto.DepositoResponse;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoResponse;
import br.com.cactusdigital.erp.estoque.dto.LoteResponse;
import br.com.cactusdigital.erp.estoque.dto.NumeroSerieResponse;
import br.com.cactusdigital.erp.estoque.dto.ConfiguracaoEstoqueEmpresaResponse;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoItemResponse;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoResponse;
import br.com.cactusdigital.erp.estoque.dto.ProdutoKitComponenteResponse;
import br.com.cactusdigital.erp.estoque.dto.SaldoEstoqueResponse;
import br.com.cactusdigital.erp.estoque.dto.TransferenciaEstoqueResponse;
import br.com.cactusdigital.erp.estoque.entity.ConfiguracaoEstoqueEmpresa;
import br.com.cactusdigital.erp.estoque.entity.Deposito;
import br.com.cactusdigital.erp.estoque.entity.EstoqueMovimento;
import br.com.cactusdigital.erp.estoque.entity.InventarioFisico;
import br.com.cactusdigital.erp.estoque.entity.InventarioFisicoItem;
import br.com.cactusdigital.erp.estoque.entity.Lote;
import br.com.cactusdigital.erp.estoque.entity.NumeroSerie;
import br.com.cactusdigital.erp.estoque.entity.ProdutoKitComponente;
import br.com.cactusdigital.erp.estoque.entity.SaldoEstoque;
import br.com.cactusdigital.erp.estoque.entity.TransferenciaEstoque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    DepositoResponse toResponse(Deposito deposito);

    LoteResponse toResponse(Lote lote);

    @Mapping(target = "depositoId", source = "deposito.id")
    @Mapping(target = "depositoNome", source = "deposito.nome")
    NumeroSerieResponse toResponse(NumeroSerie numeroSerie);

    @Mapping(target = "depositoId", source = "deposito.id")
    @Mapping(target = "depositoNome", source = "deposito.nome")
    @Mapping(target = "quantidadeDisponivel", expression = "java(saldo.getQuantidadeDisponivel())")
    SaldoEstoqueResponse toResponse(SaldoEstoque saldo);

    @Mapping(target = "depositoId", source = "deposito.id")
    @Mapping(target = "loteId", source = "lote.id")
    @Mapping(target = "numeroSerieId", source = "numeroSerie.id")
    EstoqueMovimentoResponse toResponse(EstoqueMovimento movimento);

    @Mapping(target = "depositoOrigemId", source = "depositoOrigem.id")
    @Mapping(target = "depositoOrigemNome", source = "depositoOrigem.nome")
    @Mapping(target = "depositoDestinoId", source = "depositoDestino.id")
    @Mapping(target = "depositoDestinoNome", source = "depositoDestino.nome")
    @Mapping(target = "loteId", source = "lote.id")
    TransferenciaEstoqueResponse toResponse(TransferenciaEstoque transferencia);

    @Mapping(target = "depositoId", source = "deposito.id")
    @Mapping(target = "depositoNome", source = "deposito.nome")
    InventarioFisicoResponse toResponse(InventarioFisico inventario);

    @Mapping(target = "loteId", source = "lote.id")
    @Mapping(target = "quantidadeDivergencia", expression = "java(item.getQuantidadeDivergencia())")
    InventarioFisicoItemResponse toResponse(InventarioFisicoItem item);

    ProdutoKitComponenteResponse toResponse(ProdutoKitComponente componente);

    ConfiguracaoEstoqueEmpresaResponse toResponse(ConfiguracaoEstoqueEmpresa configuracao);
}
