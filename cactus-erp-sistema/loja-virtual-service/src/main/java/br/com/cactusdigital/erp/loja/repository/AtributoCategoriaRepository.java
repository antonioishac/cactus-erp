package br.com.cactusdigital.erp.loja.repository;

import br.com.cactusdigital.erp.loja.entity.AtributoCategoria;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtributoCategoriaRepository extends JpaRepository<AtributoCategoria, Long> {

    Page<AtributoCategoria> findByCategoriaLoja_Id(Long categoriaLojaId, Pageable pageable);

    List<AtributoCategoria> findByCategoriaLoja_IdOrderByOrdemAsc(Long categoriaLojaId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code opcoes} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT a FROM AtributoCategoria a LEFT JOIN FETCH a.opcoes WHERE a.id IN :ids")
    List<AtributoCategoria> buscarComOpcoes(@Param("ids") List<Long> ids);
}
