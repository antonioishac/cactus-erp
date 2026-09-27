package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

    Page<ContaReceber> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<ContaReceber> findByClienteId(Long clienteId, Pageable pageable);

    boolean existsByOrigemTipoAndOrigemId(br.com.cactusdigital.erp.financeiro.entity.OrigemContaReceber origemTipo, Long origemId);

    /** Usado por {@code ContaReceberService#estornarPorVenda} — compensação de saga chamada pelo {@code vendas-service} (ver {@code common.saga.SagaOrchestrator}). */
    List<ContaReceber> findByOrigemTipoAndOrigemId(br.com.cactusdigital.erp.financeiro.entity.OrigemContaReceber origemTipo, Long origemId);

    /** Usado por {@code FluxoCaixaService} — só títulos ainda em aberto (ABERTA/PARCIAL), é fluxo de caixa PROJETADO, não realizado. */
    List<ContaReceber> findByEmpresaIdAndDataVencimentoBetweenAndSituacaoIn(
            Long empresaId, LocalDate dataInicio, LocalDate dataFim, List<SituacaoTitulo> situacoes);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code baixas} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT c FROM ContaReceber c LEFT JOIN FETCH c.baixas WHERE c.id IN :ids")
    List<ContaReceber> buscarComBaixas(@Param("ids") List<Long> ids);
}
