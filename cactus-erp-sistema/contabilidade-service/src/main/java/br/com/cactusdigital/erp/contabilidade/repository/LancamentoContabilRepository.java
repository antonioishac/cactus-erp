package br.com.cactusdigital.erp.contabilidade.repository;

import br.com.cactusdigital.erp.contabilidade.entity.LancamentoContabil;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoContabilRepository extends JpaRepository<LancamentoContabil, Long> {

    Page<LancamentoContabil> findByEmpresaIdAndDataLancamentoBetween(
            Long empresaId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    Page<LancamentoContabil> findByEmpresaId(Long empresaId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} da coleção {@code partidas} (+ os `ManyToOne` de dentro dela,
     * sem risco de {@code MultipleBagFetchException} — só 1 coleção na query) pra evitar N+1,
     * ver {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("""
            SELECT DISTINCT l FROM LancamentoContabil l
            LEFT JOIN FETCH l.partidas p
            LEFT JOIN FETCH p.conta
            LEFT JOIN FETCH p.centroCusto
            WHERE l.id IN :ids
            """)
    List<LancamentoContabil> buscarComPartidas(@Param("ids") List<Long> ids);

    /**
     * Usado só pelo export SPED ECD ({@code SpedContabilExportService}) — precisa de TODOS
     * os lançamentos do período numa lista só (não paginado), com as partidas já carregadas.
     *
     * <p>Deliberadamente SEM filtro por {@code situacao}: um lançamento {@code ESTORNADO}
     * continua sendo um registro real do Livro Diário (o original nunca é apagado, só
     * marcado — ver Javadoc de {@link LancamentoContabil}), e a legislação exige que TODO
     * lançamento do período apareça no I200/I250, estorno incluso — excluir só o original
     * (achado real, corrigido 2026-10-11 junto do mesmo bug em
     * {@code PartidaContabilRepository}) deixaria o arquivo SPED reportando só a metade de
     * um par que deveria somar zero.
     */
    @Query("""
            SELECT DISTINCT l FROM LancamentoContabil l
            LEFT JOIN FETCH l.partidas p
            LEFT JOIN FETCH p.conta
            LEFT JOIN FETCH p.centroCusto
            WHERE l.empresaId = :empresaId
              AND l.dataLancamento BETWEEN :dataInicio AND :dataFim
            ORDER BY l.dataLancamento ASC, l.id ASC
            """)
    List<LancamentoContabil> buscarLancadosNoPeriodoComPartidas(
            @Param("empresaId") Long empresaId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}
