package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.vendas.dto.ComissaoVendedorPeriodoResponse;
import br.com.cactusdigital.erp.vendas.dto.ProdutoMaisVendidoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaPeriodoItemResponse;
import br.com.cactusdigital.erp.vendas.entity.SituacaoVenda;
import br.com.cactusdigital.erp.vendas.entity.VendaCabecalho;
import br.com.cactusdigital.erp.vendas.entity.VendaComissao;
import br.com.cactusdigital.erp.vendas.entity.VendaItem;
import br.com.cactusdigital.erp.vendas.repository.VendaCabecalhoRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaComissaoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relatórios gerenciais de vendas — calculados sob demanda a partir do ledger de
 * {@link VendaCabecalho}/{@link VendaComissao} já existente, mesmo critério de
 * {@code CurvaAbcService} (controle-estoque-service): sem snapshot persistido, sem
 * agregação via JPQL {@code GROUP BY}/{@code SELECT new} (sem precedente no monorepo até
 * agora) — busca a lista já filtrada por período e agrega em memória com Streams, mesmo
 * estilo já usado em todo o resto do sistema.
 */
@Service
@RequiredArgsConstructor
public class RelatorioVendasService {

    private final VendaCabecalhoRepository vendaRepository;
    private final VendaComissaoRepository comissaoRepository;

    /** Só vendas {@code CONFIRMADA} — {@code RASCUNHO}/{@code CANCELADA} não são venda de fato. */
    @Transactional(readOnly = true)
    public List<VendaPeriodoItemResponse> vendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long vendedorId, Long clienteId) {
        return vendaRepository.findBySituacaoAndDataVendaBetween(SituacaoVenda.CONFIRMADA, dataInicio, dataFim).stream()
                .filter(venda -> vendedorId == null || vendedorId.equals(venda.getVendedor().getId()))
                .filter(venda -> clienteId == null || clienteId.equals(venda.getClienteId()))
                .sorted(Comparator.comparing(VendaCabecalho::getDataVenda))
                .map(venda -> new VendaPeriodoItemResponse(
                        venda.getId(), venda.getDataVenda(), venda.getEmpresaId(),
                        venda.getVendedor().getId(), venda.getClienteId(), venda.getValorTotal()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoMaisVendidoResponse> produtosMaisVendidos(LocalDate dataInicio, LocalDate dataFim) {
        List<Long> idsVendas = vendaRepository.findBySituacaoAndDataVendaBetween(SituacaoVenda.CONFIRMADA, dataInicio, dataFim).stream()
                .map(VendaCabecalho::getId)
                .toList();
        if (idsVendas.isEmpty()) {
            return List.of();
        }

        Map<Long, BigDecimal> quantidadePorProduto = new LinkedHashMap<>();
        Map<Long, BigDecimal> valorPorProduto = new LinkedHashMap<>();
        for (VendaCabecalho venda : vendaRepository.buscarComItens(idsVendas)) {
            for (VendaItem item : venda.getItens()) {
                quantidadePorProduto.merge(item.getProdutoId(), item.getQuantidade(), BigDecimal::add);
                BigDecimal valorItem = item.getValorTotal() != null ? item.getValorTotal() : BigDecimal.ZERO;
                valorPorProduto.merge(item.getProdutoId(), valorItem, BigDecimal::add);
            }
        }

        return quantidadePorProduto.entrySet().stream()
                .map(entrada -> new ProdutoMaisVendidoResponse(
                        entrada.getKey(), entrada.getValue(), valorPorProduto.get(entrada.getKey())))
                .sorted(Comparator.comparing(ProdutoMaisVendidoResponse::quantidadeTotal).reversed())
                .toList();
    }

    /** Agrupado por {@code dataLancamento} da comissão, não pela data da venda em si. */
    @Transactional(readOnly = true)
    public List<ComissaoVendedorPeriodoResponse> comissoesPorVendedor(LocalDate dataInicio, LocalDate dataFim) {
        Map<Long, BigDecimal> valorPorVendedor = new LinkedHashMap<>();
        Map<Long, Long> quantidadePorVendedor = new LinkedHashMap<>();
        for (VendaComissao comissao : comissaoRepository.findByDataLancamentoBetween(dataInicio, dataFim)) {
            Long vendedorId = comissao.getVendedor().getId();
            valorPorVendedor.merge(vendedorId, comissao.getValorComissao(), BigDecimal::add);
            quantidadePorVendedor.merge(vendedorId, 1L, Long::sum);
        }

        return valorPorVendedor.entrySet().stream()
                .map(entrada -> new ComissaoVendedorPeriodoResponse(
                        entrada.getKey(), entrada.getValue(), quantidadePorVendedor.get(entrada.getKey())))
                .sorted(Comparator.comparing(ComissaoVendedorPeriodoResponse::valorComissaoTotal).reversed())
                .toList();
    }
}
