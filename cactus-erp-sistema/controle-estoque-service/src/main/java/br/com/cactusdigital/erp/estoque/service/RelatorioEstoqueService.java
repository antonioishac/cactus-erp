package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoResponse;
import br.com.cactusdigital.erp.estoque.dto.SaldoEstoqueResponse;
import br.com.cactusdigital.erp.estoque.entity.EstoqueMovimento;
import br.com.cactusdigital.erp.estoque.entity.SaldoEstoque;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.EstoqueMovimentoRepository;
import br.com.cactusdigital.erp.estoque.repository.SaldoEstoqueRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relatórios gerenciais do controle de estoque — saldo por depósito e ficha de
 * movimentação por produto (`Curva ABC` já tinha seu próprio {@code CurvaAbcService}
 * desde o piloto da Fase 17). Calculados sob demanda a partir do próprio
 * {@link SaldoEstoque}/{@link EstoqueMovimento} já existentes, mesmo critério de
 * {@code RelatorioComprasService}/{@code RelatorioVendasService}: sem snapshot
 * persistido, injeta o repositório direto (não o service de CRUD paginado).
 */
@Service
@RequiredArgsConstructor
public class RelatorioEstoqueService {

    private final SaldoEstoqueRepository saldoRepository;
    private final EstoqueMovimentoRepository movimentoRepository;
    private final EstoqueMapper mapper;

    /** Mesmos filtros opcionais de {@code GET /saldos}, sem paginação — o documento
     * exportado é o saldo inteiro, não uma página de grade. */
    @Transactional(readOnly = true)
    public List<SaldoEstoqueResponse> saldoPorDeposito(Long produtoId, Long depositoId) {
        return saldoRepository.buscarParaRelatorio(produtoId, depositoId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Ledger cronológico de um produto — {@code depositoId} opcional estreita pra um
     * único depósito, aplicado em memória (mesmo critério de filtro opcional já usado em
     * {@code RelatorioComprasService#pedidosPorPeriodo}). */
    @Transactional(readOnly = true)
    public List<EstoqueMovimentoResponse> fichaMovimentacao(Long produtoId, Long depositoId, LocalDate dataInicio, LocalDate dataFim) {
        return movimentoRepository
                .findByProdutoIdAndDataMovimentoGreaterThanEqualAndDataMovimentoLessThanOrderByDataMovimentoAsc(
                        produtoId, inicioDoDia(dataInicio), fimExclusivo(dataFim))
                .stream()
                .filter(movimento -> depositoId == null || depositoId.equals(movimento.getDeposito().getId()))
                .map(mapper::toResponse)
                .toList();
    }

    /** Mesmo critério já usado no {@code fiscal-service} pros exports SPED (ver
     * {@code SpedFiscalIcmsIpiExportService}): limite superior exclusivo, evita incluir o
     * instante exato da meia-noite do dia seguinte. */
    private Instant inicioDoDia(LocalDate data) {
        return data.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private Instant fimExclusivo(LocalDate data) {
        return data.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
