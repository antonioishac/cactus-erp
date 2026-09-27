package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendaOrcamento;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendaOrcamentoRepository extends JpaRepository<VendaOrcamento, Long> {

    Page<VendaOrcamento> findByClienteId(Long clienteId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT o FROM VendaOrcamento o LEFT JOIN FETCH o.itens WHERE o.id IN :ids")
    List<VendaOrcamento> buscarComItens(@Param("ids") List<Long> ids);
}
