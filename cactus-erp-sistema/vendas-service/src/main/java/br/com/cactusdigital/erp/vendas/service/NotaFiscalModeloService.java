package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalModeloRequest;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalModeloResponse;
import br.com.cactusdigital.erp.vendas.entity.NotaFiscalModelo;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.NotaFiscalModeloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo (Ato Cotepe 46/10) — lookup simples, mesmo critério de {@code ComissaoPerfilService}. */
@Service
@RequiredArgsConstructor
public class NotaFiscalModeloService {

    private final NotaFiscalModeloRepository repository;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<NotaFiscalModeloResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NotaFiscalModeloResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public NotaFiscalModeloResponse criar(NotaFiscalModeloRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public NotaFiscalModeloResponse atualizar(Long id, NotaFiscalModeloRequest request) {
        NotaFiscalModelo entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    NotaFiscalModelo buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Modelo de nota fiscal", id));
    }
}
