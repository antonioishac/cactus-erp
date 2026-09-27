package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.RetornoBancario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RetornoBancarioRepository extends JpaRepository<RetornoBancario, Long> {

    Page<RetornoBancario> findByContaBancariaId(Long contaBancariaId, Pageable pageable);

    @Query("SELECT DISTINCT r FROM RetornoBancario r LEFT JOIN FETCH r.itens WHERE r.id IN :ids")
    List<RetornoBancario> buscarComItens(@Param("ids") List<Long> ids);
}
