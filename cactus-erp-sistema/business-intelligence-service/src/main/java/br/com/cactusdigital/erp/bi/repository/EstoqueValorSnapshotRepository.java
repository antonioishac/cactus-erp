package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.EstoqueValorSnapshot;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstoqueValorSnapshotRepository extends JpaRepository<EstoqueValorSnapshot, Long> {

    Page<EstoqueValorSnapshot> findByDataReferencia(LocalDate dataReferencia, Pageable pageable);

    Page<EstoqueValorSnapshot> findByDataReferenciaAndDepositoId(LocalDate dataReferencia, Long depositoId, Pageable pageable);

    /** {@code @Modifying} bulk delete — executa imediatamente contra o banco, ao
     * contrário de um {@code deleteByX} derivado sem {@code @Modifying} (que faz
     * {@code SELECT} + {@code remove()} entidade por entidade, ficando pendente no
     * contexto de persistência até o flush de fim de transação — achado real: o
     * {@code saveAll()} seguinte inseria antes desse flush acontecer, violando a
     * constraint de unicidade). */
    @Modifying
    @Query("DELETE FROM EstoqueValorSnapshot e WHERE e.dataReferencia = :dataReferencia")
    void deleteByDataReferencia(@Param("dataReferencia") LocalDate dataReferencia);
}
