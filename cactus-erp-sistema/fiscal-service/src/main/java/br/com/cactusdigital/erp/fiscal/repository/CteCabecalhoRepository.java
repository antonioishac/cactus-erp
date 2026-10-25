package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse;
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

    /** CT-e disponíveis pra um MDF-e novo — mesma regra de {@code MdfeCabecalhoRepository#buscarCteIdsJaVinculados}, invertida. */
    @Query("""
            SELECT new br.com.cactusdigital.erp.fiscal.dto.CteDisponivelMdfeResponse(
                c.id, c.serie, c.numero, c.vendaId, c.valorTotalCarga, c.pesoBruto)
            FROM CteCabecalho c
            WHERE c.empresaId = :empresaId
              AND c.situacao = br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal.AUTORIZADA
              AND NOT EXISTS (
                  SELECT 1 FROM MdfeDocumentoVinculado d
                  WHERE d.cteCabecalho = c
                    AND d.mdfeCabecalho.situacao <> br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal.CANCELADA)
            ORDER BY c.serie ASC, c.numero ASC
            """)
    List<br.com.cactusdigital.erp.fiscal.dto.CteDisponivelMdfeResponse> buscarDisponiveisParaMdfe(@Param("empresaId") Long empresaId);

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

    /** Projeção plana pro relatório unificado "notas emitidas por período" — ver Javadoc
     * de {@code NotaFiscalEletronicaRepository#buscarParaRelatorio}. {@code valorTotalServico}
     * faz o papel de "valor total" (é o valor do frete em si, distinto de
     * {@code valorTotalCarga}, que é o valor da mercadoria transportada). */
    @Query("""
            SELECT new br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse(
                'CTE', CAST(c.numero AS string), c.serie, c.dataEmissao, c.situacao, c.valorTotalServico)
            FROM CteCabecalho c
            WHERE c.dataEmissao >= :inicio AND c.dataEmissao < :fim
            AND (:empresaId IS NULL OR c.empresaId = :empresaId)
            """)
    List<NotaFiscalPeriodoItemResponse> buscarParaRelatorio(
            @Param("inicio") Instant inicio, @Param("fim") Instant fim, @Param("empresaId") Long empresaId);
}
