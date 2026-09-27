package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.SaldoEstoque;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaldoEstoqueRepository extends JpaRepository<SaldoEstoque, Long> {

    /**
     * Soma o saldo de cada produto em todos os depósitos de uma empresa — usado pelo
     * {@code sincronizacao-service} (Fase 6) pra compor o pull de produto pro PDV, que
     * enxerga um total por loja, não por depósito. Cada linha do resultado:
     * {@code [produtoId, somaQuantidade, somaQuantidadeReservada]}.
     */
    @Query("""
            SELECT s.produtoId, SUM(s.quantidade), SUM(s.quantidadeReservada)
            FROM SaldoEstoque s
            WHERE s.deposito.empresaId = :empresaId AND s.produtoId IN :produtoIds
            GROUP BY s.produtoId
            """)
    List<Object[]> agregarPorEmpresaEProdutos(@Param("empresaId") Long empresaId, @Param("produtoIds") List<Long> produtoIds);

    Optional<SaldoEstoque> findByProdutoIdAndDepositoId(Long produtoId, Long depositoId);

    Page<SaldoEstoque> findByProdutoId(Long produtoId, Pageable pageable);

    Page<SaldoEstoque> findByDepositoId(Long depositoId, Pageable pageable);

    /** Sem paginação — uso interno pra abrir inventário físico (snapshot de todos os saldos do depósito). */
    List<SaldoEstoque> findAllByDepositoIdAndQuantidadeGreaterThan(Long depositoId, java.math.BigDecimal quantidadeMinima);

    List<SaldoEstoque> findByProdutoIdIn(List<Long> produtoIds);
}
