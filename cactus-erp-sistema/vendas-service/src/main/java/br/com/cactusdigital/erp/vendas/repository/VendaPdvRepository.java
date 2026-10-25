package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendaPdv;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * {@code VendaPdv} tem 3 coleções irmãs (itens/pagamentos/devoluções) — nenhuma combinação delas cabe
 * numa {@code JOIN FETCH} só ({@code MultipleBagFetchException}), então são hidratadas "por lado", uma
 * query cada, mesma técnica já usada em {@code Pessoa}/{@code OsAbertura}.
 */
public interface VendaPdvRepository extends JpaRepository<VendaPdv, Long> {

    boolean existsByRegistroSincronizacaoId(Long registroSincronizacaoId);

    Optional<VendaPdv> findByEmpresaIdAndIdOrigem(Long empresaId, String idOrigem);

    @Query("""
            SELECT v FROM VendaPdv v
            WHERE v.empresaId = :empresaId
              AND v.dataHoraVenda >= :inicio AND v.dataHoraVenda < :fim
              AND (:pdvCaixaId IS NULL OR v.pdvCaixaId = :pdvCaixaId)
            """)
    Page<VendaPdv> buscarPorPeriodo(@Param("empresaId") Long empresaId, @Param("inicio") Instant inicio,
            @Param("fim") Instant fim, @Param("pdvCaixaId") Long pdvCaixaId, Pageable pageable);

    /** Ids das vendas {@code FECHADA} no intervalo, de todas as empresas — mesmo escopo dos relatórios B2B. */
    @Query("""
            SELECT v.id FROM VendaPdv v
            WHERE v.situacao = br.com.cactusdigital.erp.vendas.entity.SituacaoVendaPdv.FECHADA
              AND v.dataHoraVenda >= :inicio AND v.dataHoraVenda < :fim
            """)
    List<Long> buscarIdsFechadasNoPeriodo(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

    @Query("SELECT DISTINCT v FROM VendaPdv v LEFT JOIN FETCH v.itens WHERE v.id IN :ids")
    List<VendaPdv> hidratarItens(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT v FROM VendaPdv v LEFT JOIN FETCH v.pagamentos WHERE v.id IN :ids")
    List<VendaPdv> hidratarPagamentos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT v FROM VendaPdv v LEFT JOIN FETCH v.devolucoes WHERE v.id IN :ids")
    List<VendaPdv> hidratarDevolucoes(@Param("ids") List<Long> ids);
}
