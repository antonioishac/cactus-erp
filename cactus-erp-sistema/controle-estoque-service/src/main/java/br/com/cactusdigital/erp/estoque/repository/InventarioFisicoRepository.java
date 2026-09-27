package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.InventarioFisico;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventarioFisicoRepository extends JpaRepository<InventarioFisico, Long> {

    Page<InventarioFisico> findByDepositoId(Long depositoId, Pageable pageable);

    /** Evita N+1 na coleção `itens` que {@code InventarioFisicoResponse} expõe numa
     * listagem paginada — chamada só pelo efeito colateral de hidratar a coleção nas
     * entidades já gerenciadas da página. Mesmo princípio de
     * {@code ClienteRepository#hidratarEnderecos} (cadastro-base-service). */
    @Query("SELECT DISTINCT i FROM InventarioFisico i LEFT JOIN FETCH i.itens WHERE i.id IN :ids")
    List<InventarioFisico> hidratarItens(@Param("ids") List<Long> ids);
}
