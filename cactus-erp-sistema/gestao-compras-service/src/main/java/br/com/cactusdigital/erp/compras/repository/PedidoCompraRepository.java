package br.com.cactusdigital.erp.compras.repository;

import br.com.cactusdigital.erp.compras.entity.PedidoCompra;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {

    Page<PedidoCompra> findByFornecedorId(Long fornecedorId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 ao montar a página com os itens já
     * carregados — ver {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT p FROM PedidoCompra p LEFT JOIN FETCH p.itens WHERE p.id IN :ids")
    List<PedidoCompra> buscarComItens(@Param("ids") List<Long> ids);

    /** Mesma técnica acima, aplicada a uma lista não paginada (relatório por período) —
     * evita N+1 ao contar {@code itens.size()} de cada pedido na agregação em memória. */
    @Query("SELECT DISTINCT p FROM PedidoCompra p LEFT JOIN FETCH p.itens WHERE p.dataPedido BETWEEN :inicio AND :fim")
    List<PedidoCompra> buscarComItensPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
