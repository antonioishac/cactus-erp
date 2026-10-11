package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.ComprasFornecedorResumo;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComprasFornecedorResumoRepository extends JpaRepository<ComprasFornecedorResumo, Long> {

    Page<ComprasFornecedorResumo> findByAnoMes(String anoMes, Pageable pageable);

    /** Variante não paginada — usada pela exportação PDF/Excel (documento inteiro, não
     * uma página de grade). */
    List<ComprasFornecedorResumo> findByAnoMes(String anoMes);

    /** {@code @Modifying} bulk delete — ver Javadoc de
     * {@code EstoqueValorSnapshotRepository#deleteByDataReferencia}. */
    @Modifying
    @Query("DELETE FROM ComprasFornecedorResumo c WHERE c.anoMes = :anoMes")
    void deleteByAnoMes(@Param("anoMes") String anoMes);
}
