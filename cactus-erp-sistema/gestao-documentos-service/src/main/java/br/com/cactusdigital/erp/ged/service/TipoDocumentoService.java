package br.com.cactusdigital.erp.ged.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.ged.dto.TipoDocumentoRequest;
import br.com.cactusdigital.erp.ged.dto.TipoDocumentoResponse;
import br.com.cactusdigital.erp.ged.entity.TipoDocumento;
import br.com.cactusdigital.erp.ged.exception.GedErrorCode;
import br.com.cactusdigital.erp.ged.mapper.GedMapper;
import br.com.cactusdigital.erp.ged.repository.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoDocumentoService {

    private final TipoDocumentoRepository repository;
    private final GedMapper mapper;

    @Transactional(readOnly = true)
    public Page<TipoDocumentoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TipoDocumentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public TipoDocumentoResponse criar(TipoDocumentoRequest request) {
        exigirNomeDisponivel(request.nome(), null);
        TipoDocumento tipoDocumento = TipoDocumento.builder()
                .nome(request.nome())
                .tamanhoMaximoMb(request.tamanhoMaximoMb())
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(tipoDocumento));
    }

    @Transactional
    public TipoDocumentoResponse atualizar(Long id, TipoDocumentoRequest request) {
        TipoDocumento tipoDocumento = buscarEntidade(id);
        exigirNomeDisponivel(request.nome(), id);
        tipoDocumento.setNome(request.nome());
        tipoDocumento.setTamanhoMaximoMb(request.tamanhoMaximoMb());
        tipoDocumento.setSituacao(request.situacao());
        return mapper.toResponse(tipoDocumento);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private void exigirNomeDisponivel(String nome, Long idAtual) {
        repository.findByNome(nome)
                .filter(existente -> idAtual == null || !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new ConflictException(GedErrorCode.TIPO_DOCUMENTO_NOME_DUPLICADO);
                });
    }

    TipoDocumento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Tipo de documento", id));
    }
}
