package br.com.cactusdigital.erp.bi.service;

import br.com.cactusdigital.erp.bi.client.CurvaAbcItemClientResponse;
import br.com.cactusdigital.erp.bi.client.EstoqueClient;
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
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquestra o ETL do BI — lê as 3 fontes somente-leitura direto via JDBC (bulk, ver
 * {@code DataSourceLeituraConfig}) + uma chamada HTTP ao controle-estoque-service pra
 * Curva ABC (reaproveita a lógica de classificação já existente lá, não duplica aqui).
 * Ordem importa: dimensões antes de fatos (fatos resolvem nome via dimensão já
 * atualizada); cada geração de snapshot é idempotente por chave (deleta a mesma
 * referência antes de reinserir — reprocessável sem duplicar).
 */
@Service
@RequiredArgsConstructor
public class EtlService {

    private static final Logger log = LoggerFactory.getLogger(EtlService.class);
    private static final List<String> SITUACOES_PEDIDO_CONSIDERADAS =
            List.of("ENVIADO", "PARCIALMENTE_RECEBIDO", "RECEBIDO_TOTAL");
    private static final int JANELA_DIAS_CURVA_ABC = 90;

    private final JdbcTemplate cadastroJdbcTemplate;
    private final JdbcTemplate estoqueJdbcTemplate;
    private final JdbcTemplate comprasJdbcTemplate;
    private final EstoqueClient estoqueClient;

    private final ProdutoDimRepository produtoDimRepository;
    private final FornecedorDimRepository fornecedorDimRepository;
    private final DepositoDimRepository depositoDimRepository;
    private final EstoqueValorSnapshotRepository estoqueValorSnapshotRepository;
    private final CurvaAbcSnapshotRepository curvaAbcSnapshotRepository;
    private final ComprasFornecedorResumoRepository comprasFornecedorResumoRepository;

    @Scheduled(cron = "${cactus.bi.etl.cron:0 0 2 * * *}")
    public void executarAgendado() {
        log.info("Iniciando ETL agendado do BI");
        executar(LocalDate.now());
    }

    @Transactional
    public void executar(LocalDate dataReferencia) {
        atualizarDimensoes();
        gerarEstoqueValorSnapshot(dataReferencia);
        gerarCurvaAbcSnapshot(dataReferencia);
        gerarComprasFornecedorResumo(YearMonth.from(dataReferencia));
        log.info("ETL do BI concluído pra dataReferencia={}", dataReferencia);
    }

    void atualizarDimensoes() {
        atualizarProdutoDim();
        atualizarFornecedorDim();
        atualizarDepositoDim();
    }

    private void atualizarProdutoDim() {
        List<ProdutoDim> dims = cadastroJdbcTemplate.query(
                "SELECT id, nome, codigo_interno, situacao FROM produto",
                (rs, rowNum) -> ProdutoDim.builder()
                        .produtoId(rs.getLong("id"))
                        .nome(rs.getString("nome"))
                        .codigoInterno(rs.getString("codigo_interno"))
                        .situacao(Situacao.fromCodigo(rs.getString("situacao").charAt(0)))
                        .build());
        produtoDimRepository.deleteAllInBatch();
        produtoDimRepository.saveAll(dims);
    }

    private void atualizarFornecedorDim() {
        List<FornecedorDim> dims = cadastroJdbcTemplate.query(
                "SELECT f.id, p.nome FROM fornecedor f JOIN pessoa p ON p.id = f.pessoa_id",
                (rs, rowNum) -> FornecedorDim.builder()
                        .fornecedorId(rs.getLong("id"))
                        .nome(rs.getString("nome"))
                        .build());
        fornecedorDimRepository.deleteAllInBatch();
        fornecedorDimRepository.saveAll(dims);
    }

    private void atualizarDepositoDim() {
        List<DepositoDim> dims = estoqueJdbcTemplate.query(
                "SELECT id, nome, empresa_id FROM deposito",
                (rs, rowNum) -> DepositoDim.builder()
                        .depositoId(rs.getLong("id"))
                        .nome(rs.getString("nome"))
                        .empresaId(rs.getLong("empresa_id"))
                        .build());
        depositoDimRepository.deleteAllInBatch();
        depositoDimRepository.saveAll(dims);
    }

    private void gerarEstoqueValorSnapshot(LocalDate dataReferencia) {
        List<EstoqueValorSnapshot> snapshots = estoqueJdbcTemplate.query(
                "SELECT deposito_id, produto_id, quantidade, custo_medio_ponderado FROM saldo_estoque WHERE quantidade > 0",
                (rs, rowNum) -> {
                    BigDecimal quantidade = rs.getBigDecimal("quantidade");
                    BigDecimal custoMedio = rs.getBigDecimal("custo_medio_ponderado");
                    return EstoqueValorSnapshot.builder()
                            .dataReferencia(dataReferencia)
                            .depositoId(rs.getLong("deposito_id"))
                            .produtoId(rs.getLong("produto_id"))
                            .quantidade(quantidade)
                            .custoMedioPonderado(custoMedio)
                            .valorTotal(quantidade.multiply(custoMedio))
                            .build();
                });
        estoqueValorSnapshotRepository.deleteByDataReferencia(dataReferencia);
        estoqueValorSnapshotRepository.saveAll(snapshots);
    }

    private void gerarCurvaAbcSnapshot(LocalDate dataReferencia) {
        List<Long> depositosAtivos = estoqueJdbcTemplate.queryForList(
                "SELECT id FROM deposito WHERE situacao = 'A'", Long.class);

        for (Long depositoId : depositosAtivos) {
            List<CurvaAbcItemClientResponse> itens = estoqueClient.buscarCurvaAbc(depositoId, JANELA_DIAS_CURVA_ABC);
            curvaAbcSnapshotRepository.deleteByDataReferenciaAndDepositoId(dataReferencia, depositoId);
            List<CurvaAbcSnapshot> snapshots = itens.stream()
                    .<CurvaAbcSnapshot>map(item -> CurvaAbcSnapshot.builder()
                            .dataReferencia(dataReferencia)
                            .depositoId(depositoId)
                            .produtoId(item.produtoId())
                            .valorMovimentado(item.valorMovimentado())
                            .percentualParticipacao(item.percentualParticipacao())
                            .percentualAcumulado(item.percentualAcumulado())
                            .classificacao(item.classificacao())
                            .build())
                    .toList();
            curvaAbcSnapshotRepository.saveAll(snapshots);
        }
    }

    private void gerarComprasFornecedorResumo(YearMonth anoMes) {
        LocalDate primeiroDia = anoMes.atDay(1);
        LocalDate ultimoDia = anoMes.atEndOfMonth();
        String placeholders = String.join(",", SITUACOES_PEDIDO_CONSIDERADAS.stream().map(s -> "?").toList());

        Object[] parametros = {
                primeiroDia, ultimoDia,
                SITUACOES_PEDIDO_CONSIDERADAS.get(0), SITUACOES_PEDIDO_CONSIDERADAS.get(1), SITUACOES_PEDIDO_CONSIDERADAS.get(2)
        };
        List<ComprasFornecedorResumo> resumos = comprasJdbcTemplate.query(
                """
                SELECT fornecedor_id, COUNT(*) AS quantidade_pedidos, SUM(valor_total) AS valor_total_comprado
                FROM pedido_compra
                WHERE COALESCE(data_pedido, DATE(data_criacao)) BETWEEN ? AND ?
                  AND situacao IN (%s)
                GROUP BY fornecedor_id
                """.formatted(placeholders),
                (rs, rowNum) -> ComprasFornecedorResumo.builder()
                        .anoMes(anoMes.toString())
                        .fornecedorId(rs.getLong("fornecedor_id"))
                        .quantidadePedidos(rs.getInt("quantidade_pedidos"))
                        .valorTotalComprado(rs.getBigDecimal("valor_total_comprado"))
                        .build(),
                parametros);

        comprasFornecedorResumoRepository.deleteByAnoMes(anoMes.toString());
        comprasFornecedorResumoRepository.saveAll(resumos);
    }
}
