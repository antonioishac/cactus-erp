package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse;
import br.com.cactusdigital.erp.fiscal.repository.CteCabecalhoRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalConsumidorRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalEletronicaRepository;
import br.com.cactusdigital.erp.fiscal.repository.NotaFiscalServicoRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notas emitidas por período — unifica os 4 tipos de documento fiscal do módulo
 * (NF-e/NFC-e/NFS-e/CT-e) numa grade só, achatada via {@code SELECT new} em cada
 * repositório (ver Javadoc de {@code NotaFiscalEletronicaRepository#buscarParaRelatorio})
 * — nunca toca as coleções {@code itens}/{@code componentes} de cada documento, o
 * relatório é um resumo, não o detalhamento item a item.
 */
@Service
@RequiredArgsConstructor
public class RelatorioFiscalService {

    private final NotaFiscalEletronicaRepository nfeRepository;
    private final NotaFiscalConsumidorRepository nfceRepository;
    private final NotaFiscalServicoRepository nfseRepository;
    private final CteCabecalhoRepository cteRepository;

    @Transactional(readOnly = true)
    public List<NotaFiscalPeriodoItemResponse> notasPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long empresaId) {
        Instant inicio = inicioDoDia(dataInicio);
        Instant fim = fimExclusivo(dataFim);
        return Stream.of(
                        nfeRepository.buscarParaRelatorio(inicio, fim, empresaId),
                        nfceRepository.buscarParaRelatorio(inicio, fim, empresaId),
                        nfseRepository.buscarParaRelatorio(inicio, fim, empresaId),
                        cteRepository.buscarParaRelatorio(inicio, fim, empresaId))
                .flatMap(List::stream)
                .sorted(Comparator.comparing(NotaFiscalPeriodoItemResponse::dataEmissao))
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
