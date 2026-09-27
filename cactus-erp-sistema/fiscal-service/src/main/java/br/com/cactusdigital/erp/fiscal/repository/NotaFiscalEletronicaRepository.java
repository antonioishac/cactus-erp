package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaFiscalEletronicaRepository extends JpaRepository<NotaFiscalEletronica, Long> {

    boolean existsByVendaId(Long vendaId);

    Optional<NotaFiscalEletronica> findByVendaId(Long vendaId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT n FROM NotaFiscalEletronica n LEFT JOIN FETCH n.itens WHERE n.id IN :ids")
    List<NotaFiscalEletronica> buscarComItens(@Param("ids") List<Long> ids);

    /** Usado pelo export SPED Fiscal (EFD ICMS/IPI e EFD Contribuições, Fase 15) — só notas
     * {@code AUTORIZADA} entram no arquivo digital, nunca {@code RASCUNHO}/{@code ENVIADA}. */
    @Query("""
            SELECT DISTINCT n FROM NotaFiscalEletronica n
            LEFT JOIN FETCH n.itens
            WHERE n.empresaId = :empresaId
              AND n.situacao = br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal.AUTORIZADA
              AND n.dataEmissao >= :inicio AND n.dataEmissao < :fim
            ORDER BY n.dataEmissao ASC
            """)
    List<NotaFiscalEletronica> buscarAutorizadasNoPeriodoComItens(
            @Param("empresaId") Long empresaId, @Param("inicio") Instant inicio, @Param("fim") Instant fim);
}
