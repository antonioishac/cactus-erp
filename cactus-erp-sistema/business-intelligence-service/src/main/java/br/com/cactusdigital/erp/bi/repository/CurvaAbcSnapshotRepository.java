package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.CurvaAbcSnapshot;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CurvaAbcSnapshotRepository extends JpaRepository<CurvaAbcSnapshot, Long> {

    Page<CurvaAbcSnapshot> findByDataReferencia(LocalDate dataReferencia, Pageable pageable);

    Page<CurvaAbcSnapshot> findByDataReferenciaAndDepositoId(LocalDate dataReferencia, Long depositoId, Pageable pageable);

    /** {@code @Modifying} bulk delete — ver Javadoc de
     * {@code EstoqueValorSnapshotRepository#deleteByDataReferencia}. */
    @Modifying
    @Query("DELETE FROM CurvaAbcSnapshot c WHERE c.dataReferencia = :dataReferencia AND c.depositoId = :depositoId")
    void deleteByDataReferenciaAndDepositoId(@Param("dataReferencia") LocalDate dataReferencia, @Param("depositoId") Long depositoId);
}
