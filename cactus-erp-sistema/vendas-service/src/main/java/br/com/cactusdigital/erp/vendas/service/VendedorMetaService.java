package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.VendedorMetaRequest;
import br.com.cactusdigital.erp.vendas.dto.VendedorMetaResponse;
import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import br.com.cactusdigital.erp.vendas.entity.VendedorMeta;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.VendedorMetaRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Meta de venda do vendedor por cliente/período (AFV) — sub-recurso de {@link Vendedor}. */
@Service
@RequiredArgsConstructor
public class VendedorMetaService {

    private final VendedorMetaRepository repository;
    private final VendedorService vendedorService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public List<VendedorMetaResponse> listarPorVendedor(Long vendedorId) {
        return repository.findByVendedorId(vendedorId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public VendedorMetaResponse adicionar(Long vendedorId, VendedorMetaRequest request) {
        Vendedor vendedor = vendedorService.buscarEntidade(vendedorId);
        VendedorMeta meta = VendedorMeta.builder()
                .vendedor(vendedor)
                .clienteId(request.clienteId())
                .periodoMeta(request.periodoMeta())
                .metaOrcada(request.metaOrcada())
                .metaRealizada(BigDecimal.ZERO)
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .build();
        return mapper.toResponse(repository.save(meta));
    }

    @Transactional
    public void remover(Long vendedorId, Long metaId) {
        repository.delete(buscarEntidade(vendedorId, metaId));
    }

    private VendedorMeta buscarEntidade(Long vendedorId, Long metaId) {
        return repository.findById(metaId)
                .filter(candidato -> candidato.getVendedor().getId().equals(vendedorId))
                .orElseThrow(() -> NotFoundException.of("Meta do vendedor", metaId));
    }
}
