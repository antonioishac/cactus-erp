package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.ProdutoLoja;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoLojaRepository extends JpaRepository<ProdutoLoja, Long> {

    Page<ProdutoLoja> findByLojaId(Long lojaId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code variantes} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT p FROM ProdutoLoja p LEFT JOIN FETCH p.variantes WHERE p.id IN :ids")
    List<ProdutoLoja> buscarComVariantes(@Param("ids") List<Long> ids);
}
