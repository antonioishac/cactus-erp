package br.com.cactusdigital.erp.os.service;

import br.com.cactusdigital.erp.os.dto.OsAberturaResponse;
import br.com.cactusdigital.erp.os.entity.OsAbertura;
import br.com.cactusdigital.erp.os.mapper.OsMapper;
import br.com.cactusdigital.erp.os.repository.OsAberturaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ordens de serviço por período/status — calculado sob demanda a partir do próprio
 * {@link OsAbertura}, mesmo critério de {@code RelatorioDocumentoService}
 * (gestao-documentos-service): reaproveita {@code OsMapper#toResponse}, mas só depois de
 * hidratar as 3 coleções irmãs (equipamentos/itens/evolucoes) que {@link OsAberturaResponse}
 * expõe — pular a hidratação não evita N+1 (o mapper monta o record inteiro de qualquer
 * jeito), mesmo achado real já corrigido em
 * {@code cadastro-base-service.RelatorioCadastroService}.
 */
@Service
@RequiredArgsConstructor
public class RelatorioOsService {

    private final OsAberturaRepository repository;
    private final OsMapper mapper;

    @Transactional(readOnly = true)
    public List<OsAberturaResponse> osPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long osStatusId, Long empresaId) {
        List<OsAbertura> itens = repository.buscarParaRelatorio(dataInicio, dataFim, osStatusId, empresaId);
        if (!itens.isEmpty()) {
            List<Long> ids = itens.stream().map(OsAbertura::getId).toList();
            repository.hidratarItens(ids);
            repository.hidratarEvolucoes(ids);
        }
        return itens.stream().map(mapper::toResponse).toList();
    }
}
