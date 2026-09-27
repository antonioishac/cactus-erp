package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.FormaPagamentoRequest;
import br.com.cactusdigital.erp.cadastro.dto.FormaPagamentoResponse;
import br.com.cactusdigital.erp.cadastro.entity.FormaPagamento;
import br.com.cactusdigital.erp.cadastro.mapper.EmpresaMapper;
import br.com.cactusdigital.erp.cadastro.repository.FormaPagamentoRepository;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Validado contra {@code BANCO_PDV.md.PDV_TIPO_PAGAMENTO} — ver {@link FormaPagamento}. */
@Service
@RequiredArgsConstructor
public class FormaPagamentoService {

    private final FormaPagamentoRepository repository;
    private final EmpresaMapper mapper;

    @Transactional(readOnly = true)
    public Page<FormaPagamentoResponse> listar(String descricao, Pageable pageable) {
        Page<FormaPagamento> pagina = StringUtils.hasText(descricao)
                ? repository.findByDescricaoContainingIgnoreCase(descricao, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    /** {@code desde} = cursor de sincronização incremental, ver {@code FormaPagamentoRepository#buscarDesde}. */
    @Transactional(readOnly = true)
    public Page<FormaPagamentoResponse> listar(Instant desde, Pageable pageable) {
        return repository.buscarDesde(desde, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FormaPagamentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public FormaPagamentoResponse criar(FormaPagamentoRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    public FormaPagamentoResponse atualizar(Long id, FormaPagamentoRequest request) {
        FormaPagamento entidade = buscarEntidade(id);
        mapper.atualizar(request, entidade);
        return mapper.toResponse(entidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private FormaPagamento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Forma de pagamento", id));
    }
}
