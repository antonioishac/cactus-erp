package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidor;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaFiscalConsumidorRepository extends JpaRepository<NotaFiscalConsumidor, Long> {

    boolean existsByPedidoId(Long pedidoId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT n FROM NotaFiscalConsumidor n LEFT JOIN FETCH n.itens WHERE n.id IN :ids")
    List<NotaFiscalConsumidor> buscarComItens(@Param("ids") List<Long> ids);

    /** Projeção plana pro relatório unificado "notas emitidas por período" — ver Javadoc
     * de {@code NotaFiscalEletronicaRepository#buscarParaRelatorio}. */
    @Query("""
            SELECT new br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse(
                'NFCE', CAST(n.numero AS string), n.serie, n.dataEmissao, n.situacao, n.valorTotal)
            FROM NotaFiscalConsumidor n
            WHERE n.dataEmissao >= :inicio AND n.dataEmissao < :fim
            AND (:empresaId IS NULL OR n.empresaId = :empresaId)
            """)
    List<NotaFiscalPeriodoItemResponse> buscarParaRelatorio(
            @Param("inicio") Instant inicio, @Param("fim") Instant fim, @Param("empresaId") Long empresaId);
}
