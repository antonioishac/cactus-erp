package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.BaixarKitRequest;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoRequest;
import br.com.cactusdigital.erp.estoque.dto.ProdutoKitComponenteRequest;
import br.com.cactusdigital.erp.estoque.dto.ProdutoKitComponenteResponse;
import br.com.cactusdigital.erp.estoque.entity.OrigemMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.entity.ProdutoKitComponente;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.ProdutoKitComponenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * BOM simples: {@code baixar()} gera {@code SAIDA} (origem {@code KIT}) em cascata pra
 * cada componente, na quantidade {@code quantidadeVendida × componente.quantidade} —
 * reaproveitável por quem processar venda de kit no futuro (Fase 6, sincronização).
 */
@Service
@RequiredArgsConstructor
public class ProdutoKitService {

    private final ProdutoKitComponenteRepository repository;
    private final EstoqueMovimentoService movimentoService;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public java.util.List<ProdutoKitComponenteResponse> listarComponentes(Long produtoKitId) {
        return repository.findByProdutoKitId(produtoKitId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public ProdutoKitComponenteResponse adicionarComponente(Long produtoKitId, ProdutoKitComponenteRequest request) {
        if (produtoKitId.equals(request.produtoComponenteId())) {
            throw new BusinessException(EstoqueErrorCode.KIT_COMPONENTE_CIRCULAR);
        }
        ProdutoKitComponente componente = ProdutoKitComponente.builder()
                .produtoKitId(produtoKitId)
                .produtoComponenteId(request.produtoComponenteId())
                .quantidade(request.quantidade())
                .build();
        return mapper.toResponse(repository.save(componente));
    }

    @Transactional
    public void removerComponente(Long produtoKitId, Long componenteId) {
        ProdutoKitComponente componente = repository.findById(componenteId)
                .filter(c -> c.getProdutoKitId().equals(produtoKitId))
                .orElseThrow(() -> NotFoundException.of("Componente do kit", componenteId));
        repository.delete(componente);
    }

    @Transactional
    public void baixar(Long produtoKitId, BaixarKitRequest request, Long usuarioId) {
        for (ProdutoKitComponente componente : repository.findByProdutoKitId(produtoKitId)) {
            movimentoService.registrar(new EstoqueMovimentoRequest(
                    componente.getProdutoComponenteId(), request.depositoId(), null, null,
                    TipoMovimentoEstoque.SAIDA, null, componente.getQuantidade().multiply(request.quantidade()), null,
                    OrigemMovimentoEstoque.KIT, produtoKitId, null,
                    "Baixa em cascata — kit #" + produtoKitId), usuarioId);
        }
    }
}
