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

    /**
     * Variante não paginada, com {@code JOIN FETCH} do depósito — usada pela exportação
     * PDF/Excel (documento inteiro, não uma página de grade). {@code JOIN FETCH}
     * necessário aqui, diferente de {@code EstoqueMovimentoRepository} (que só expõe
     * {@code depositoId}, seguro contra N+1 via o próprio proxy lazy): o mapeamento pra
     * {@code SaldoEstoqueResponse} lê {@code deposito.getNome()}, que de fato inicializa
     * o proxy — sem fetch antecipado, um relatório sem paginação dispararia 1 SELECT por
     * linha. Os 2 parâmetros opcionais cobrem os 3 filtros possíveis numa query só
     * (ambos nulos = todos os saldos).
     */
    @Query("""
            SELECT s FROM SaldoEstoque s JOIN FETCH s.deposito
            WHERE (:produtoId IS NULL OR s.produtoId = :produtoId)
            AND (:depositoId IS NULL OR s.deposito.id = :depositoId)
            """)
    List<SaldoEstoque> buscarParaRelatorio(@Param("produtoId") Long produtoId, @Param("depositoId") Long depositoId);

    /** Sem paginação — uso interno pra abrir inventário físico (snapshot de todos os saldos do depósito). */
    List<SaldoEstoque> findAllByDepositoIdAndQuantidadeGreaterThan(Long depositoId, java.math.BigDecimal quantidadeMinima);

    List<SaldoEstoque> findByProdutoIdIn(List<Long> produtoIds);
}
