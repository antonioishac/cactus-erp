package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.Lote;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    Page<Lote> findByProdutoId(Long produtoId, Pageable pageable);

    Optional<Lote> findByProdutoIdAndCodigo(Long produtoId, String codigo);

    /** Base pro FEFO — lotes vencendo até a data informada, mais próximos primeiro. */
    Page<Lote> findByProdutoIdAndDataValidadeLessThanEqualOrderByDataValidadeAsc(Long produtoId, LocalDate dataLimite, Pageable pageable);
}
