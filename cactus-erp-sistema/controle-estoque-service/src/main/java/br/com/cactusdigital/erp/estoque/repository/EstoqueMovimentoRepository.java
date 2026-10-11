package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.EstoqueMovimento;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueMovimentoRepository extends JpaRepository<EstoqueMovimento, Long> {

    Page<EstoqueMovimento> findByProdutoIdAndDepositoId(Long produtoId, Long depositoId, Pageable pageable);

    Page<EstoqueMovimento> findByProdutoId(Long produtoId, Pageable pageable);

    Page<EstoqueMovimento> findByDepositoId(Long depositoId, Pageable pageable);

    /** Base pra curva ABC: todas as saídas do período, pra agregar valor movimentado por produto (sem paginação de propósito). */
    List<EstoqueMovimento> findByTipoMovimentoAndDataMovimentoBetween(TipoMovimentoEstoque tipoMovimento, Instant inicio, Instant fim);

    /** Base pra ficha de movimentação (relatório) — ledger cronológico de um produto no
     * período, limite superior exclusivo (mesmo critério já usado no {@code fiscal-service}
     * pros exports SPED: {@code dataInicio.atStartOfDay}/{@code dataFim.plusDays(1).atStartOfDay},
     * evita incluir o instante exato da meia-noite do dia seguinte). */
    List<EstoqueMovimento> findByProdutoIdAndDataMovimentoGreaterThanEqualAndDataMovimentoLessThanOrderByDataMovimentoAsc(
            Long produtoId, Instant inicio, Instant fimExclusivo);
}
