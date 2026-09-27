package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {

    Page<ContaPagar> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<ContaPagar> findByFornecedorId(Long fornecedorId, Pageable pageable);

    boolean existsByOrigemTipoAndOrigemId(br.com.cactusdigital.erp.financeiro.entity.OrigemContaPagar origemTipo, Long origemId);

    /** Usado por {@code ContaPagarService#estornarPorCompra} — compensação de saga chamada pelo {@code gestao-compras-service} (ver {@code common.saga.SagaOrchestrator}). */
    List<ContaPagar> findByOrigemTipoAndOrigemId(br.com.cactusdigital.erp.financeiro.entity.OrigemContaPagar origemTipo, Long origemId);

    /** Usado por {@code FluxoCaixaService} — só títulos ainda em aberto (ABERTA/PARCIAL), é fluxo de caixa PROJETADO, não realizado. */
    List<ContaPagar> findByEmpresaIdAndDataVencimentoBetweenAndSituacaoIn(
            Long empresaId, LocalDate dataInicio, LocalDate dataFim, List<SituacaoTitulo> situacoes);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code baixas} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT c FROM ContaPagar c LEFT JOIN FETCH c.baixas WHERE c.id IN :ids")
    List<ContaPagar> buscarComBaixas(@Param("ids") List<Long> ids);
}
