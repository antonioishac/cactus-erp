package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.VendaComissaoResponse;
import br.com.cactusdigital.erp.vendas.entity.VendaComissao;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.VendaComissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta de lançamentos de comissão — gerados automaticamente por {@link VendaService#confirmar}, nunca criados diretamente via API. */
@Service
@RequiredArgsConstructor
public class VendaComissaoService {

    private final VendaComissaoRepository repository;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<VendaComissaoResponse> listar(Long vendedorId, Pageable pageable) {
        Page<VendaComissao> pagina = vendedorId != null
                ? repository.findByVendedorId(vendedorId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VendaComissaoResponse buscarPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> NotFoundException.of("Comissão de venda", id)));
    }
}
