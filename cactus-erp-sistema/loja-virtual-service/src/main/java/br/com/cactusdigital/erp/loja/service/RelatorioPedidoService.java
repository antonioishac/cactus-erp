package br.com.cactusdigital.erp.loja.service;

import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.entity.SituacaoPedido;
import br.com.cactusdigital.erp.loja.mapper.LojaVirtualMapper;
import br.com.cactusdigital.erp.loja.repository.PedidoRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pedidos da loja por período — calculado sob demanda a partir do próprio
 * {@link br.com.cactusdigital.erp.loja.entity.Pedido}, mesmo critério de
 * {@code RelatorioDocumentoService} (gestao-documentos-service): reaproveita
 * {@code LojaVirtualMapper#toResponse}, mas só via {@link PedidoRepository#buscarParaRelatorio}
 * (que já faz o {@code JOIN FETCH} de clienteLoja/itens numa query só) — nunca
 * {@code repository.findAll()} puro, que deixaria o mapper disparar N+1 ao montar
 * {@code clienteNome}/{@code clienteEmail}/{@code clienteCpf}/{@code itens} linha a linha.
 */
@Service
@RequiredArgsConstructor
public class RelatorioPedidoService {

    private final PedidoRepository repository;
    private final LojaVirtualMapper mapper;

    @Transactional(readOnly = true)
    public List<PedidoResponse> pedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long lojaId, SituacaoPedido situacao) {
        return repository.buscarParaRelatorio(inicioDoDia(dataInicio), fimExclusivo(dataFim), lojaId, situacao).stream()
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
