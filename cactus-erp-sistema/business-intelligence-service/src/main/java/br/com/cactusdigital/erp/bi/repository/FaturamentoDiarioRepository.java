package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.FaturamentoDiario;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FaturamentoDiarioRepository extends JpaRepository<FaturamentoDiario, Long> {

    /** Parâmetros nulos = sem filtro. Ordem cronológica, B2B antes de PDV no mesmo dia. */
    @Query("""
            SELECT f FROM FaturamentoDiario f
            WHERE f.dataReferencia BETWEEN :inicio AND :fim
              AND (:empresaId IS NULL OR f.empresaId = :empresaId)
              AND (:canal IS NULL OR f.canal = :canal)
            ORDER BY f.dataReferencia, f.empresaId, f.canal
            """)
    List<FaturamentoDiario> buscar(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim,
                                   @Param("empresaId") Long empresaId, @Param("canal") String canal);

    /** {@code @Modifying} bulk delete — ver Javadoc de
     * {@code EstoqueValorSnapshotRepository#deleteByDataReferencia}. */
    @Modifying
    @Query("DELETE FROM FaturamentoDiario f WHERE f.dataReferencia BETWEEN :inicio AND :fim")
    void deleteByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
