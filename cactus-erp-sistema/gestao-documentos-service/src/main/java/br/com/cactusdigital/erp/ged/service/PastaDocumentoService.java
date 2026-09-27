package br.com.cactusdigital.erp.ged.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.ged.dto.PastaDocumentoRequest;
import br.com.cactusdigital.erp.ged.dto.PastaDocumentoResponse;
import br.com.cactusdigital.erp.ged.entity.PastaDocumento;
import br.com.cactusdigital.erp.ged.exception.GedErrorCode;
import br.com.cactusdigital.erp.ged.mapper.GedMapper;
import br.com.cactusdigital.erp.ged.repository.PastaDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PastaDocumentoService {

    private final PastaDocumentoRepository repository;
    private final GedMapper mapper;

    @Transactional(readOnly = true)
    public Page<PastaDocumentoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PastaDocumentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PastaDocumentoResponse criar(PastaDocumentoRequest request) {
        PastaDocumento pasta = PastaDocumento.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .pastaPai(buscarPaiOpcional(request.pastaPaiId()))
                .build();
        return mapper.toResponse(repository.save(pasta));
    }

    @Transactional
    public PastaDocumentoResponse atualizar(Long id, PastaDocumentoRequest request) {
        PastaDocumento pasta = buscarEntidade(id);
        validarNaoCircular(id, request.pastaPaiId());
        pasta.setNome(request.nome());
        pasta.setDescricao(request.descricao());
        pasta.setPastaPai(buscarPaiOpcional(request.pastaPaiId()));
        return mapper.toResponse(pasta);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /** Impede um ciclo (pasta virar ancestral de si mesma) ao subir a cadeia de pais a
     * partir do novo pai proposto — só relevante em atualização (pasta nova nunca pode
     * ser ancestral de nada, ainda não existe). */
    private void validarNaoCircular(Long id, Long novoPastaPaiId) {
        Long atual = novoPastaPaiId;
        while (atual != null) {
            if (atual.equals(id)) {
                throw new BusinessException(GedErrorCode.PASTA_PAI_CIRCULAR);
            }
            atual = repository.findById(atual)
                    .map(PastaDocumento::getPastaPai)
                    .map(PastaDocumento::getId)
                    .orElse(null);
        }
    }

    private PastaDocumento buscarPaiOpcional(Long pastaPaiId) {
        return pastaPaiId == null ? null : buscarEntidade(pastaPaiId);
    }

    PastaDocumento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Pasta de documento", id));
    }
}
