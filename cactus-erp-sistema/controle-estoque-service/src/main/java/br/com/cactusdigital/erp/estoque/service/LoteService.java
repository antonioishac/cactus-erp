package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.LoteRequest;
import br.com.cactusdigital.erp.estoque.dto.LoteResponse;
import br.com.cactusdigital.erp.estoque.entity.Lote;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.LoteRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository repository;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<LoteResponse> listar(Long produtoId, Pageable pageable) {
        Page<Lote> pagina = produtoId != null ? repository.findByProdutoId(produtoId, pageable) : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LoteResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    /** FEFO — lotes do produto vencendo até {@code diasLimite} dias a partir de hoje, mais próximos primeiro. */
    @Transactional(readOnly = true)
    public Page<LoteResponse> listarProximosDoVencimento(Long produtoId, int diasLimite, Pageable pageable) {
        LocalDate dataLimite = LocalDate.now().plusDays(diasLimite);
        return repository.findByProdutoIdAndDataValidadeLessThanEqualOrderByDataValidadeAsc(produtoId, dataLimite, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public LoteResponse criar(LoteRequest request) {
        if (repository.findByProdutoIdAndCodigo(request.produtoId(), request.codigo()).isPresent()) {
            throw new ConflictException(EstoqueErrorCode.LOTE_JA_CADASTRADO);
        }
        Lote lote = Lote.builder()
                .produtoId(request.produtoId())
                .codigo(request.codigo())
                .dataFabricacao(request.dataFabricacao())
                .dataValidade(request.dataValidade())
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(lote));
    }

    @Transactional
    public LoteResponse atualizar(Long id, LoteRequest request) {
        Lote lote = buscarEntidade(id);
        repository.findByProdutoIdAndCodigo(request.produtoId(), request.codigo())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException(EstoqueErrorCode.LOTE_JA_CADASTRADO);
                });
        lote.setProdutoId(request.produtoId());
        lote.setCodigo(request.codigo());
        lote.setDataFabricacao(request.dataFabricacao());
        lote.setDataValidade(request.dataValidade());
        lote.setSituacao(request.situacao());
        return mapper.toResponse(lote);
    }

    private Lote buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Lote", id));
    }
}
