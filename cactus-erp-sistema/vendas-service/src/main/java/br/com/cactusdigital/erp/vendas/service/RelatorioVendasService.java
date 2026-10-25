package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.vendas.dto.CanalVenda;
import br.com.cactusdigital.erp.vendas.entity.VendaPdv;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvDevolucao;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvItem;
import br.com.cactusdigital.erp.vendas.repository.VendaPdvRepository;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
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
 * Relatórios gerenciais de vendas — calculados sob demanda a partir de {@link VendaCabecalho} (B2B),
 * {@link VendaPdv} (balcão, desde 2026-10-25 — antes o faturamento de uma loja com PDV saía subestimado) e
 * {@link VendaComissao}, mesmo critério de
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
    private final VendaPdvRepository vendaPdvRepository;

    /** Mesmo fuso de {@code VendaPdvService}: o "dia" de uma venda do PDV é o dia LOCAL da loja. */
    @Value("${cactus.vendas.pdv.fuso-horario:America/Sao_Paulo}")
    private String fusoHorario;

    /**
     * B2B ({@code CONFIRMADA}) + PDV ({@code FECHADA}), ordenado por data. {@code canal} nulo = os dois.
     * Filtrar por vendedor exclui o PDV (venda de balcão não tem vendedor). Venda do PDV entra pelo valor
     * final <b>menos o já devolvido</b>: devolução desfaz a venda daquele item.
     */
    @Transactional(readOnly = true)
    public List<VendaPeriodoItemResponse> vendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long vendedorId, Long clienteId,
            CanalVenda canal) {
        List<VendaPeriodoItemResponse> linhas = new ArrayList<>();
        if (canal == null || canal == CanalVenda.B2B) {
            vendaRepository.findBySituacaoAndDataVendaBetween(SituacaoVenda.CONFIRMADA, dataInicio, dataFim).stream()
                    .filter(venda -> vendedorId == null || vendedorId.equals(venda.getVendedor().getId()))
                    .filter(venda -> clienteId == null || clienteId.equals(venda.getClienteId()))
                    .map(venda -> new VendaPeriodoItemResponse(
                            venda.getId(), venda.getDataVenda(), venda.getEmpresaId(),
                            venda.getVendedor().getId(), venda.getClienteId(), venda.getValorTotal(), CanalVenda.B2B))
                    .forEach(linhas::add);
        }
        if ((canal == null || canal == CanalVenda.PDV) && vendedorId == null) {
            vendasPdvFechadas(dataInicio, dataFim).stream()
                    .filter(venda -> clienteId == null || clienteId.equals(venda.getClienteId()))
                    .map(venda -> new VendaPeriodoItemResponse(
                            venda.getId(), dataLocal(venda), venda.getEmpresaId(), null, venda.getClienteId(),
                            venda.getValorFinal().subtract(valorDevolvido(venda)), CanalVenda.PDV))
                    .forEach(linhas::add);
        }
        linhas.sort(Comparator.comparing(VendaPeriodoItemResponse::dataVenda).thenComparing(VendaPeriodoItemResponse::canal));
        return linhas;
    }

    /**
     * Agrega B2B + PDV por produto. No PDV, itens cancelados não entram e a quantidade/valor são líquidos de
     * devolução (o valor devolvido de cada item é abatido do valor dele).
     */
    @Transactional(readOnly = true)
    public List<ProdutoMaisVendidoResponse> produtosMaisVendidos(LocalDate dataInicio, LocalDate dataFim, CanalVenda canal) {
        Map<Long, BigDecimal> quantidadePorProduto = new LinkedHashMap<>();
        Map<Long, BigDecimal> valorPorProduto = new LinkedHashMap<>();

        if (canal == null || canal == CanalVenda.B2B) {
            List<Long> idsVendas = vendaRepository.findBySituacaoAndDataVendaBetween(SituacaoVenda.CONFIRMADA, dataInicio, dataFim).stream()
                    .map(VendaCabecalho::getId)
                    .toList();
            if (!idsVendas.isEmpty()) {
                for (VendaCabecalho venda : vendaRepository.buscarComItens(idsVendas)) {
                    for (VendaItem item : venda.getItens()) {
                        quantidadePorProduto.merge(item.getProdutoId(), item.getQuantidade(), BigDecimal::add);
                        BigDecimal valorItem = item.getValorTotal() != null ? item.getValorTotal() : BigDecimal.ZERO;
                        valorPorProduto.merge(item.getProdutoId(), valorItem, BigDecimal::add);
                    }
                }
            }
        }
        if (canal == null || canal == CanalVenda.PDV) {
            for (VendaPdv venda : vendasPdvFechadas(dataInicio, dataFim)) {
                Map<Long, BigDecimal> devolvidoPorItem = new HashMap<>();
                venda.getDevolucoes().forEach(d -> devolvidoPorItem.merge(d.getItem().getId(), d.getValorDevolvido(), BigDecimal::add));
                for (VendaPdvItem item : venda.getItens()) {
                    if (item.isCancelado()) {
                        continue;
                    }
                    BigDecimal quantidade = item.getQuantidade().subtract(item.getQuantidadeDevolvida());
                    BigDecimal valor = item.getValorTotal().subtract(devolvidoPorItem.getOrDefault(item.getId(), BigDecimal.ZERO));
                    quantidadePorProduto.merge(item.getProdutoId(), quantidade, BigDecimal::add);
                    valorPorProduto.merge(item.getProdutoId(), valor, BigDecimal::add);
                }
            }
        }

        return quantidadePorProduto.entrySet().stream()
                .filter(entrada -> entrada.getValue().signum() > 0)
                .map(entrada -> new ProdutoMaisVendidoResponse(
                        entrada.getKey(), entrada.getValue(), valorPorProduto.get(entrada.getKey())))
                .sorted(Comparator.comparing(ProdutoMaisVendidoResponse::quantidadeTotal).reversed())
                .toList();
    }

    /** Vendas do PDV {@code FECHADA} no período (por data LOCAL da loja), com itens e devoluções já carregados. */
    private List<VendaPdv> vendasPdvFechadas(LocalDate dataInicio, LocalDate dataFim) {
        ZoneId fuso = ZoneId.of(fusoHorario);
        List<Long> ids = vendaPdvRepository.buscarIdsFechadasNoPeriodo(
                dataInicio.atStartOfDay(fuso).toInstant(), dataFim.plusDays(1).atStartOfDay(fuso).toInstant());
        if (ids.isEmpty()) {
            return List.of();
        }
        vendaPdvRepository.hidratarDevolucoes(ids);
        return vendaPdvRepository.hidratarItens(ids);
    }

    private BigDecimal valorDevolvido(VendaPdv venda) {
        return venda.getDevolucoes().stream().map(VendaPdvDevolucao::getValorDevolvido).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDate dataLocal(VendaPdv venda) {
        return venda.getDataHoraVenda().atZone(ZoneId.of(fusoHorario)).toLocalDate();
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
