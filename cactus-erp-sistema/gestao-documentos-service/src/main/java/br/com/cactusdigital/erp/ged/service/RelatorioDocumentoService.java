package br.com.cactusdigital.erp.ged.service;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.ged.dto.DocumentoResponse;
import br.com.cactusdigital.erp.ged.mapper.GedMapper;
import br.com.cactusdigital.erp.ged.repository.DocumentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Documentos por pasta/tipo — calculado sob demanda a partir do próprio
 * {@link br.com.cactusdigital.erp.ged.entity.Documento}, mesmo critério de
 * {@code RelatorioCadastroService} (cadastro-base-service): reaproveita
 * {@code GedMapper#toResponse}, mas via {@link DocumentoRepository#buscarParaRelatorio}
 * (que já faz o {@code JOIN FETCH} de pasta/tipo/versões numa query só) — nunca
 * {@code repository.findAll()} puro, que deixaria o mapper disparar N+1 ao montar
 * {@code pastaNome}/{@code tipoDocumentoNome}/{@code versoes} linha a linha.
 */
@Service
@RequiredArgsConstructor
public class RelatorioDocumentoService {

    private final DocumentoRepository repository;
    private final GedMapper mapper;

    @Transactional(readOnly = true)
    public List<DocumentoResponse> documentos(Long pastaId, Long tipoDocumentoId, Situacao situacao) {
        return repository.buscarParaRelatorio(pastaId, tipoDocumentoId, situacao).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
