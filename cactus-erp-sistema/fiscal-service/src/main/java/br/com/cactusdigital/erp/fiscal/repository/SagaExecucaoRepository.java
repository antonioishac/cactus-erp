package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.entity.SagaExecucao;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SagaExecucaoRepository extends JpaRepository<SagaExecucao, Long> {

    Page<SagaExecucao> findByReferenciaTipoAndReferenciaId(String referenciaTipo, Long referenciaId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code passos} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT s FROM SagaExecucao s LEFT JOIN FETCH s.passos WHERE s.id IN :ids")
    List<SagaExecucao> buscarComPassos(@Param("ids") List<Long> ids);
}
