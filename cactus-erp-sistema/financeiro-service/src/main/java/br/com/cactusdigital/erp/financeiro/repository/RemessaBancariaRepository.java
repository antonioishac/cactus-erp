package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.RemessaBancaria;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RemessaBancariaRepository extends JpaRepository<RemessaBancaria, Long> {

    Page<RemessaBancaria> findByContaBancariaId(Long contaBancariaId, Pageable pageable);

    long countByContaBancariaId(Long contaBancariaId);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver {@code PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT r FROM RemessaBancaria r LEFT JOIN FETCH r.itens WHERE r.id IN :ids")
    List<RemessaBancaria> buscarComItens(@Param("ids") List<Long> ids);
}
