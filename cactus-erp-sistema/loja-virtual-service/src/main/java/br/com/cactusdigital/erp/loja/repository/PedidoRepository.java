package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.Pedido;
import br.com.cactusdigital.erp.loja.entity.SituacaoPedido;
import java.time.Instant;
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

    /**
     * Variante pro relatório de pedidos por período — filtro por {@code dataCriacao}
     * ({@code BaseEntity}, data do checkout), opcionalmente por loja/situação.
     * {@code JOIN FETCH} de {@code clienteLoja} (singular, resolve {@code clienteNome}/
     * {@code clienteEmail}/{@code clienteCpf}) combinado com a coleção {@code itens} (só
     * 1 bag, sem risco de {@code MultipleBagFetchException}) na mesma query.
     */
    @Query("""
            SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.clienteLoja LEFT JOIN FETCH p.itens
            WHERE p.dataCriacao >= :inicio AND p.dataCriacao < :fim
            AND (:lojaId IS NULL OR p.lojaId = :lojaId)
            AND (:situacao IS NULL OR p.situacao = :situacao)
            """)
    List<Pedido> buscarParaRelatorio(
            @Param("inicio") Instant inicio, @Param("fim") Instant fim,
            @Param("lojaId") Long lojaId, @Param("situacao") SituacaoPedido situacao);
}
