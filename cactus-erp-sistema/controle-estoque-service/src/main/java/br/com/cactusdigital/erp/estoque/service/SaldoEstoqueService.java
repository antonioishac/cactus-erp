package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.SaldoAgregadoResponse;
import br.com.cactusdigital.erp.estoque.dto.SaldoEstoqueResponse;
import br.com.cactusdigital.erp.estoque.entity.SaldoEstoque;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.SaldoEstoqueRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Só leitura — mutação de saldo é exclusiva de {@link EstoqueMovimentoService}, via ledger. */
@Service
@RequiredArgsConstructor
public class SaldoEstoqueService {

    private final SaldoEstoqueRepository repository;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<SaldoEstoqueResponse> listar(Long produtoId, Long depositoId, Pageable pageable) {
        Page<SaldoEstoque> pagina;
        if (produtoId != null) {
            pagina = repository.findByProdutoId(produtoId, pageable);
        } else if (depositoId != null) {
            pagina = repository.findByDepositoId(depositoId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SaldoEstoqueResponse buscarPorProdutoEDeposito(Long produtoId, Long depositoId) {
        return repository.findByProdutoIdAndDepositoId(produtoId, depositoId)
                .map(mapper::toResponse)
                .orElseThrow(() -> NotFoundException.of("Saldo de estoque", produtoId + "/" + depositoId));
    }

    /**
     * Soma o saldo de cada produto em todos os depósitos da empresa — usado pelo
     * {@code sincronizacao-service} (Fase 6) pra compor o pull de produto pro PDV.
     * Produto sem nenhum saldo lançado ainda não aparece na resposta (o chamador trata
     * ausência como zero, mesma semântica de "nunca movimentado").
     */
    @Transactional(readOnly = true)
    public List<SaldoAgregadoResponse> agregarPorEmpresaEProdutos(Long empresaId, List<Long> produtoIds) {
        if (produtoIds == null || produtoIds.isEmpty()) {
            return List.of();
        }
        return repository.agregarPorEmpresaEProdutos(empresaId, produtoIds).stream()
                .map(linha -> {
                    Long produtoId = (Long) linha[0];
                    BigDecimal total = (BigDecimal) linha[1];
                    BigDecimal reservada = (BigDecimal) linha[2];
                    return new SaldoAgregadoResponse(produtoId, total, reservada, total.subtract(reservada));
                })
                .toList();
    }
}
