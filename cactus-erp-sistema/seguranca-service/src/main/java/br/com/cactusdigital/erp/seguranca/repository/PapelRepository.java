package br.com.cactusdigital.erp.seguranca.repository;

import br.com.cactusdigital.erp.seguranca.entity.Papel;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    Page<Papel> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /** Rebusca com `funcionalidades` já carregado — usado por {@code PapelService.listar} via {@code PaginatedFetchSupport} pra evitar N+1 na listagem paginada (achado real, 2026-09-02). */
    @Query("SELECT DISTINCT p FROM Papel p LEFT JOIN FETCH p.funcionalidades WHERE p.id IN :ids")
    List<Papel> findAllByIdComFuncionalidades(@Param("ids") List<Long> ids);
}
