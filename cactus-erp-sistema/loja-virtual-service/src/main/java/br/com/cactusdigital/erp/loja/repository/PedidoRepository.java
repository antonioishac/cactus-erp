package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.Pedido;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByLojaId(Long lojaId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id IN :ids")
    List<Pedido> buscarComItens(@Param("ids") List<Long> ids);
}
