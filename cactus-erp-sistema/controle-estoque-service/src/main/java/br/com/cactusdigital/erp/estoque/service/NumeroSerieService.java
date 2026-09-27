package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.NumeroSerieRequest;
import br.com.cactusdigital.erp.estoque.dto.NumeroSerieResponse;
import br.com.cactusdigital.erp.estoque.entity.Deposito;
import br.com.cactusdigital.erp.estoque.entity.NumeroSerie;
import br.com.cactusdigital.erp.estoque.entity.SituacaoNumeroSerie;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.NumeroSerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NumeroSerieService {

    private final NumeroSerieRepository repository;
    private final DepositoService depositoService;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<NumeroSerieResponse> listarPorProduto(Long produtoId, Pageable pageable) {
        return repository.findByProdutoId(produtoId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NumeroSerieResponse> listarDisponiveisNoDeposito(Long depositoId, Pageable pageable) {
        return repository.findByDepositoIdAndSituacao(depositoId, SituacaoNumeroSerie.DISPONIVEL, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NumeroSerieResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public NumeroSerieResponse cadastrar(NumeroSerieRequest request) {
        if (repository.findByProdutoIdAndNumeroSerie(request.produtoId(), request.numeroSerie()).isPresent()) {
            throw new ConflictException(EstoqueErrorCode.NUMERO_SERIE_JA_CADASTRADO);
        }
        Deposito deposito = request.depositoId() != null ? depositoService.buscarEntidade(request.depositoId()) : null;
        NumeroSerie numeroSerie = NumeroSerie.builder()
                .produtoId(request.produtoId())
                .numeroSerie(request.numeroSerie())
                .deposito(deposito)
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(numeroSerie));
    }

    /** Muda a situação (e opcionalmente o depósito atual) — usado em venda/baixa/transferência unitária. */
    @Transactional
    public NumeroSerieResponse atualizarSituacao(Long id, SituacaoNumeroSerie situacao, Long depositoId) {
        NumeroSerie numeroSerie = buscarEntidade(id);
        numeroSerie.setSituacao(situacao);
        numeroSerie.setDeposito(depositoId != null ? depositoService.buscarEntidade(depositoId) : null);
        return mapper.toResponse(numeroSerie);
    }

    private NumeroSerie buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Número de série", id));
    }
}
