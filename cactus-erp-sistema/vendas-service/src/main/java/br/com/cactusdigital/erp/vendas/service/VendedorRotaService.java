package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.vendas.dto.VendedorRotaRequest;
import br.com.cactusdigital.erp.vendas.dto.VendedorRotaResponse;
import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import br.com.cactusdigital.erp.vendas.entity.VendedorRota;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.VendedorRotaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Roteiro de visita do vendedor (AFV) — sub-recurso de {@link Vendedor}, sem listagem paginada própria (rota é sempre pequena, mesmo critério de {@code ComissaoObjetivoService}). */
@Service
@RequiredArgsConstructor
public class VendedorRotaService {

    private final VendedorRotaRepository repository;
    private final VendedorService vendedorService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public List<VendedorRotaResponse> listarPorVendedor(Long vendedorId) {
        return repository.findByVendedorIdOrderByPosicaoAsc(vendedorId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public VendedorRotaResponse adicionar(Long vendedorId, VendedorRotaRequest request) {
        Vendedor vendedor = vendedorService.buscarEntidade(vendedorId);
        VendedorRota rota = VendedorRota.builder()
                .vendedor(vendedor)
                .clienteId(request.clienteId())
                .posicao(request.posicao())
                .build();
        return mapper.toResponse(repository.save(rota));
    }

    @Transactional
    public void remover(Long vendedorId, Long rotaId) {
        VendedorRota rota = buscarEntidade(vendedorId, rotaId);
        repository.delete(rota);
    }

    private VendedorRota buscarEntidade(Long vendedorId, Long rotaId) {
        return repository.findById(rotaId)
                .filter(candidato -> candidato.getVendedor().getId().equals(vendedorId))
                .orElseThrow(() -> NotFoundException.of("Item de rota do vendedor", rotaId));
    }
}
