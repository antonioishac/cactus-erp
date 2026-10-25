package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.entity.MdfeCabecalho;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MdfeCabecalhoRepository extends JpaRepository<MdfeCabecalho, Long> {

    /** {@code documentos} (bag única) + o {@code cteCabecalho} singular de cada um na mesma
     * query — o mapper lê série/número/chave do CT-e, então sem o fetch do lado singular o
     * N+1 só mudaria de lugar. Ver {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("""
            SELECT DISTINCT m FROM MdfeCabecalho m
            LEFT JOIN FETCH m.documentos d
            LEFT JOIN FETCH d.cteCabecalho
            WHERE m.id IN :ids
            """)
    List<MdfeCabecalho> buscarComDocumentos(@Param("ids") List<Long> ids);

    /** CT-e (dentre os informados) já vinculados a um MDF-e NÃO cancelado — cancelado libera o CT-e (ver {@code V8__mdfe_encerramento_cancelamento.sql}). */
    @Query("""
            SELECT d.cteCabecalho.id FROM MdfeDocumentoVinculado d
            WHERE d.cteCabecalho.id IN :cteIds
              AND d.mdfeCabecalho.situacao <> br.com.cactusdigital.erp.fiscal.entity.SituacaoNotaFiscal.CANCELADA
            """)
    List<Long> buscarCteIdsJaVinculados(@Param("cteIds") List<Long> cteIds);
}
