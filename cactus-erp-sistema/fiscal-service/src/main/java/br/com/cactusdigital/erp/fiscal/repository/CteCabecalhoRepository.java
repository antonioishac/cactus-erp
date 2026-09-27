package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.entity.CteCabecalho;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CteCabecalhoRepository extends JpaRepository<CteCabecalho, Long> {

    boolean existsByVendaId(Long vendaId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code componentes} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT c FROM CteCabecalho c LEFT JOIN FETCH c.componentes WHERE c.id IN :ids")
    List<CteCabecalho> buscarComComponentes(@Param("ids") List<Long> ids);

    /** Usado pelo export SPED Fiscal — Bloco D (EFD ICMS/IPI), só CT-e {@code AUTORIZADA}. */
    @Query("""
            SELECT DISTINCT c FROM CteCabecalho c
            LEFT JOIN FETCH c.componentes
            WHERE c.empresaId = :empresaId
              AND c.situacao = br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal.AUTORIZADA
              AND c.dataEmissao >= :inicio AND c.dataEmissao < :fim
            ORDER BY c.dataEmissao ASC
            """)
    List<CteCabecalho> buscarAutorizadosNoPeriodoComComponentes(
            @Param("empresaId") Long empresaId, @Param("inicio") Instant inicio, @Param("fim") Instant fim);
}
