package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesPagamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendaCondicoesPagamentoRepository extends JpaRepository<VendaCondicoesPagamento, Long> {

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code parcelas} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT c FROM VendaCondicoesPagamento c LEFT JOIN FETCH c.parcelas WHERE c.id IN :ids")
    List<VendaCondicoesPagamento> buscarComParcelas(@Param("ids") List<Long> ids);
}
