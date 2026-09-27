package br.com.cactusdigital.erp.financeiro.repository;

import br.com.cactusdigital.erp.financeiro.entity.ExtratoBancarioItem;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExtratoBancarioItemRepository extends JpaRepository<ExtratoBancarioItem, Long> {

    Page<ExtratoBancarioItem> findByImportacaoId(Long importacaoId, Pageable pageable);

    List<ExtratoBancarioItem> findByContaBancariaIdAndConciliadoFalse(Long contaBancariaId);

    /** Usado por {@code ConciliacaoBancariaService#importar} pra filtrar {@code fitId}
     * já importados antes de inserir — dedup entre importações. */
    @Query("SELECT i.fitId FROM ExtratoBancarioItem i WHERE i.contaBancariaId = :contaBancariaId AND i.fitId IN :fitIds")
    Set<String> buscarFitIdsExistentes(@Param("contaBancariaId") Long contaBancariaId, @Param("fitIds") Set<String> fitIds);
}
