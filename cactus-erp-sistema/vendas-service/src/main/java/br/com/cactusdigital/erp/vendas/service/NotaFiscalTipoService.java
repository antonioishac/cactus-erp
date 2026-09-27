package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalTipoRequest;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalTipoResponse;
import br.com.cactusdigital.erp.vendas.entity.NotaFiscalModelo;
import br.com.cactusdigital.erp.vendas.entity.NotaFiscalTipo;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.NotaFiscalTipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Controla série/numeração de referência por modelo de NF — resolve a FK pro {@link NotaFiscalModelo} no service (mesmo critério de {@code Produto} no cadastro-base). */
@Service
@RequiredArgsConstructor
public class NotaFiscalTipoService {

    private final NotaFiscalTipoRepository repository;
    private final NotaFiscalModeloService modeloService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<NotaFiscalTipoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NotaFiscalTipoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public NotaFiscalTipoResponse criar(NotaFiscalTipoRequest request) {
        NotaFiscalModelo modelo = modeloService.buscarEntidade(request.notaFiscalModeloId());
        NotaFiscalTipo tipo = NotaFiscalTipo.builder()
                .notaFiscalModelo(modelo)
                .nome(request.nome())
                .descricao(request.descricao())
                .serie(request.serie())
                .serieScan(request.serieScan())
                .ultimoNumero(0)
                .situacao(request.situacao())
                .build();
        return mapper.toResponse(repository.save(tipo));
    }

    @Transactional
    public NotaFiscalTipoResponse atualizar(Long id, NotaFiscalTipoRequest request) {
        NotaFiscalTipo tipo = buscarEntidade(id);
        tipo.setNotaFiscalModelo(modeloService.buscarEntidade(request.notaFiscalModeloId()));
        tipo.setNome(request.nome());
        tipo.setDescricao(request.descricao());
        tipo.setSerie(request.serie());
        tipo.setSerieScan(request.serieScan());
        tipo.setSituacao(request.situacao());
        return mapper.toResponse(tipo);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    NotaFiscalTipo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Tipo de nota fiscal", id));
    }
}
