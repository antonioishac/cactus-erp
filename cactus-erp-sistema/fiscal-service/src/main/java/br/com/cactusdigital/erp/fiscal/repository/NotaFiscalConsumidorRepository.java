package br.com.cactusdigital.erp.fiscal.repository;

import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotaFiscalConsumidorRepository extends JpaRepository<NotaFiscalConsumidor, Long> {

    boolean existsByPedidoId(Long pedidoId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT n FROM NotaFiscalConsumidor n LEFT JOIN FETCH n.itens WHERE n.id IN :ids")
    List<NotaFiscalConsumidor> buscarComItens(@Param("ids") List<Long> ids);
}
