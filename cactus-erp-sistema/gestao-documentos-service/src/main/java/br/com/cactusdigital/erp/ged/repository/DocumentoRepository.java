package br.com.cactusdigital.erp.ged.repository;

import br.com.cactusdigital.erp.ged.entity.Documento;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Page<Documento> findByEntidadeTipoAndEntidadeId(String entidadeTipo, Long entidadeId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 ao montar a página com as versões já
     * carregadas — ver {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT d FROM Documento d LEFT JOIN FETCH d.versoes WHERE d.id IN :ids")
    List<Documento> buscarComVersoes(@Param("ids") List<Long> ids);
}
