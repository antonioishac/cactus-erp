package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalServico;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaFiscalServicoRepository extends JpaRepository<NotaFiscalServico, Long> {

    boolean existsByOsAberturaId(Long osAberturaId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT n FROM NotaFiscalServico n LEFT JOIN FETCH n.itens WHERE n.id IN :ids")
    List<NotaFiscalServico> buscarComItens(@Param("ids") List<Long> ids);

    /** Usado pelo export EFD Contribuições — Bloco A, só NFS-e {@code AUTORIZADA}. */
    @Query("""
            SELECT DISTINCT n FROM NotaFiscalServico n
            LEFT JOIN FETCH n.itens i
            LEFT JOIN FETCH i.listaServico
            WHERE n.empresaId = :empresaId
              AND n.situacao = br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal.AUTORIZADA
              AND n.dataEmissao >= :inicio AND n.dataEmissao < :fim
            ORDER BY n.dataEmissao ASC
            """)
    List<NotaFiscalServico> buscarAutorizadasNoPeriodoComItens(
            @Param("empresaId") Long empresaId, @Param("inicio") Instant inicio, @Param("fim") Instant fim);

    /** Projeção plana pro relatório unificado "notas emitidas por período" — ver Javadoc
     * de {@code NotaFiscalEletronicaRepository#buscarParaRelatorio}. {@code numero} já é
     * {@code String} nesta entidade (diferente de NF-e/NFC-e/CT-e, que usam {@code Long}),
     * sem precisar de {@code CAST}. NFS-e não tem "série" no sentido tradicional de
     * NFe/NFCe/CTe — {@code serieRps} (série do RPS que originou a nota) ocupa a mesma
     * coluna no relatório, mais útil que um literal {@code NULL} em constructor
     * expression JPQL (evitado deliberadamente, risco de ambiguidade de tipo). {@code
     * valorLiquido} faz o papel de "valor total". */
    @Query("""
            SELECT new br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse(
                'NFSE', n.numero, n.serieRps, n.dataEmissao, n.situacao, n.valorLiquido)
            FROM NotaFiscalServico n
            WHERE n.dataEmissao >= :inicio AND n.dataEmissao < :fim
            AND (:empresaId IS NULL OR n.empresaId = :empresaId)
            """)
    List<NotaFiscalPeriodoItemResponse> buscarParaRelatorio(
            @Param("inicio") Instant inicio, @Param("fim") Instant fim, @Param("empresaId") Long empresaId);
}
