package br.com.cactusdigital.erp.bi.service;

import br.com.cactusdigital.erp.bi.dto.ComprasFornecedorIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.CurvaAbcIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.EstoqueValorIndicadorResponse;
import br.com.cactusdigital.erp.bi.entity.ComprasFornecedorResumo;
import br.com.cactusdigital.erp.bi.entity.CurvaAbcSnapshot;
import br.com.cactusdigital.erp.bi.entity.DepositoDim;
import br.com.cactusdigital.erp.bi.entity.EstoqueValorSnapshot;
import br.com.cactusdigital.erp.bi.entity.FornecedorDim;
import br.com.cactusdigital.erp.bi.entity.ProdutoDim;
import br.com.cactusdigital.erp.bi.repository.ComprasFornecedorResumoRepository;
import br.com.cactusdigital.erp.bi.repository.CurvaAbcSnapshotRepository;
import br.com.cactusdigital.erp.bi.repository.DepositoDimRepository;
import br.com.cactusdigital.erp.bi.repository.EstoqueValorSnapshotRepository;
import br.com.cactusdigital.erp.bi.repository.FornecedorDimRepository;
import br.com.cactusdigital.erp.bi.repository.ProdutoDimRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * API de leitura sobre o schema de BI — resolve nome de produto/depósito/fornecedor a
 * partir das dimensões em lote (uma consulta por página, não uma por linha — mesmo
 * cuidado de N+1 já aplicado no resto do monorepo via
 * {@code common.jpa.PaginatedFetchSupport}, aqui replicado manualmente porque o formato
 * é diferente: resolução de nome por lookup de dimensão, não fetch de coleção JPA).
 */
@Service
@RequiredArgsConstructor
public class IndicadoresService {

    private final EstoqueValorSnapshotRepository estoqueValorSnapshotRepository;
    private final CurvaAbcSnapshotRepository curvaAbcSnapshotRepository;
    private final ComprasFornecedorResumoRepository comprasFornecedorResumoRepository;
    private final ProdutoDimRepository produtoDimRepository;
    private final FornecedorDimRepository fornecedorDimRepository;
    private final DepositoDimRepository depositoDimRepository;

    @Transactional(readOnly = true)
    public Page<EstoqueValorIndicadorResponse> estoqueValor(LocalDate dataReferencia, Long depositoId, Pageable pageable) {
        Page<EstoqueValorSnapshot> pagina = depositoId != null
                ? estoqueValorSnapshotRepository.findByDataReferenciaAndDepositoId(dataReferencia, depositoId, pageable)
                : estoqueValorSnapshotRepository.findByDataReferencia(dataReferencia, pageable);

        Map<Long, String> nomesProduto = nomesProduto(pagina.getContent().stream().map(EstoqueValorSnapshot::getProdutoId).toList());
        Map<Long, String> nomesDeposito = nomesDeposito(pagina.getContent().stream().map(EstoqueValorSnapshot::getDepositoId).toList());

        return pagina.map(item -> new EstoqueValorIndicadorResponse(
                item.getDataReferencia(), item.getDepositoId(), nomesDeposito.get(item.getDepositoId()),
                item.getProdutoId(), nomesProduto.get(item.getProdutoId()),
                item.getQuantidade(), item.getCustoMedioPonderado(), item.getValorTotal()));
    }

    @Transactional(readOnly = true)
    public Page<CurvaAbcIndicadorResponse> curvaAbc(LocalDate dataReferencia, Long depositoId, Pageable pageable) {
        Page<CurvaAbcSnapshot> pagina = depositoId != null
                ? curvaAbcSnapshotRepository.findByDataReferenciaAndDepositoId(dataReferencia, depositoId, pageable)
                : curvaAbcSnapshotRepository.findByDataReferencia(dataReferencia, pageable);

        Map<Long, String> nomesProduto = nomesProduto(pagina.getContent().stream().map(CurvaAbcSnapshot::getProdutoId).toList());
        Map<Long, String> nomesDeposito = nomesDeposito(pagina.getContent().stream().map(CurvaAbcSnapshot::getDepositoId).toList());

        return pagina.map(item -> new CurvaAbcIndicadorResponse(
                item.getDataReferencia(), item.getDepositoId(), nomesDeposito.get(item.getDepositoId()),
                item.getProdutoId(), nomesProduto.get(item.getProdutoId()),
                item.getValorMovimentado(), item.getPercentualParticipacao(), item.getPercentualAcumulado(), item.getClassificacao()));
    }

    @Transactional(readOnly = true)
    public Page<ComprasFornecedorIndicadorResponse> comprasFornecedor(String anoMes, Pageable pageable) {
        Page<ComprasFornecedorResumo> pagina = comprasFornecedorResumoRepository.findByAnoMes(anoMes, pageable);
        Map<Long, String> nomesFornecedor = nomesFornecedor(pagina.getContent().stream().map(ComprasFornecedorResumo::getFornecedorId).toList());

        return pagina.map(item -> new ComprasFornecedorIndicadorResponse(
                item.getAnoMes(), item.getFornecedorId(), nomesFornecedor.get(item.getFornecedorId()),
                item.getQuantidadePedidos(), item.getValorTotalComprado()));
    }

    // -------------------------------------------------------- Exportação PDF/Excel
    // Mesmo cálculo/resolução de nome dos 3 métodos paginados acima, sem Pageable —
    // a exportação é o documento inteiro, não uma página de grade.

    @Transactional(readOnly = true)
    public List<EstoqueValorIndicadorResponse> estoqueValor(LocalDate dataReferencia, Long depositoId) {
        List<EstoqueValorSnapshot> itens = depositoId != null
                ? estoqueValorSnapshotRepository.findByDataReferenciaAndDepositoId(dataReferencia, depositoId)
                : estoqueValorSnapshotRepository.findByDataReferencia(dataReferencia);

        Map<Long, String> nomesProduto = nomesProduto(itens.stream().map(EstoqueValorSnapshot::getProdutoId).toList());
        Map<Long, String> nomesDeposito = nomesDeposito(itens.stream().map(EstoqueValorSnapshot::getDepositoId).toList());

        return itens.stream()
                .map(item -> new EstoqueValorIndicadorResponse(
                        item.getDataReferencia(), item.getDepositoId(), nomesDeposito.get(item.getDepositoId()),
                        item.getProdutoId(), nomesProduto.get(item.getProdutoId()),
                        item.getQuantidade(), item.getCustoMedioPonderado(), item.getValorTotal()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CurvaAbcIndicadorResponse> curvaAbc(LocalDate dataReferencia, Long depositoId) {
        List<CurvaAbcSnapshot> itens = depositoId != null
                ? curvaAbcSnapshotRepository.findByDataReferenciaAndDepositoId(dataReferencia, depositoId)
                : curvaAbcSnapshotRepository.findByDataReferencia(dataReferencia);

        Map<Long, String> nomesProduto = nomesProduto(itens.stream().map(CurvaAbcSnapshot::getProdutoId).toList());
        Map<Long, String> nomesDeposito = nomesDeposito(itens.stream().map(CurvaAbcSnapshot::getDepositoId).toList());

        return itens.stream()
                .map(item -> new CurvaAbcIndicadorResponse(
                        item.getDataReferencia(), item.getDepositoId(), nomesDeposito.get(item.getDepositoId()),
                        item.getProdutoId(), nomesProduto.get(item.getProdutoId()),
                        item.getValorMovimentado(), item.getPercentualParticipacao(), item.getPercentualAcumulado(), item.getClassificacao()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComprasFornecedorIndicadorResponse> comprasFornecedor(String anoMes) {
        List<ComprasFornecedorResumo> itens = comprasFornecedorResumoRepository.findByAnoMes(anoMes);
        Map<Long, String> nomesFornecedor = nomesFornecedor(itens.stream().map(ComprasFornecedorResumo::getFornecedorId).toList());

        return itens.stream()
                .map(item -> new ComprasFornecedorIndicadorResponse(
                        item.getAnoMes(), item.getFornecedorId(), nomesFornecedor.get(item.getFornecedorId()),
                        item.getQuantidadePedidos(), item.getValorTotalComprado()))
                .toList();
    }

    private Map<Long, String> nomesProduto(List<Long> ids) {
        return mapaDeNomes(ids, produtoDimRepository::findByProdutoIdIn, ProdutoDim::getProdutoId, ProdutoDim::getNome);
    }

    private Map<Long, String> nomesDeposito(List<Long> ids) {
        return mapaDeNomes(ids, depositoDimRepository::findByDepositoIdIn, DepositoDim::getDepositoId, DepositoDim::getNome);
    }

    private Map<Long, String> nomesFornecedor(List<Long> ids) {
        return mapaDeNomes(ids, fornecedorDimRepository::findByFornecedorIdIn, FornecedorDim::getFornecedorId, FornecedorDim::getNome);
    }

    private <T> Map<Long, String> mapaDeNomes(
            List<Long> ids, Function<List<Long>, List<T>> buscarPorIds, Function<T, Long> idExtractor, Function<T, String> nomeExtractor) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return buscarPorIds.apply(ids.stream().distinct().toList()).stream()
                .collect(Collectors.toMap(idExtractor, nomeExtractor));
    }
}
