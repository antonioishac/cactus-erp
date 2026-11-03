package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.NumeroFiscalPulado;
import br.com.cactusdigital.erp.cadastro.entity.TipoDocumentoFiscal;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NumeroFiscalPuladoRepository extends JpaRepository<NumeroFiscalPulado, Long> {

    Page<NumeroFiscalPulado> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<NumeroFiscalPulado> findByEmpresaIdAndDataRegularizacaoIsNull(Long empresaId, Pageable pageable);

    /** Marca como regularizados os pendentes da faixa. Bulk update de propósito: uma faixa pode ter milhares de números. */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE NumeroFiscalPulado n
               SET n.protocoloInutilizacao = :protocolo, n.dataRegularizacao = :data
             WHERE n.empresaId = :empresaId
               AND n.tipoDocumento = :tipo
               AND ((:serie IS NULL AND n.serie IS NULL) OR n.serie = :serie)
               AND n.numero BETWEEN :inicial AND :final
               AND n.dataRegularizacao IS NULL
            """)
    int regularizarFaixa(@Param("empresaId") Long empresaId, @Param("tipo") TipoDocumentoFiscal tipo, @Param("serie") String serie,
                         @Param("inicial") Long inicial, @Param("final") Long numeroFinal,
                         @Param("protocolo") String protocolo, @Param("data") Instant data);
}
