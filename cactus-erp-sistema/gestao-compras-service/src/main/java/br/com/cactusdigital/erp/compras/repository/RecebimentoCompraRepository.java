package br.com.cactusdigital.erp.compras.repository;

import br.com.cactusdigital.erp.compras.entity.RecebimentoCompra;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecebimentoCompraRepository extends JpaRepository<RecebimentoCompra, Long> {

    Page<RecebimentoCompra> findByPedidoId(Long pedidoId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 ao montar a página com os itens já
     * carregados — ver {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT r FROM RecebimentoCompra r LEFT JOIN FETCH r.itens WHERE r.id IN :ids")
    List<RecebimentoCompra> buscarComItens(@Param("ids") List<Long> ids);
}
