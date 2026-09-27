package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.NumeroSerie;
import br.com.cactusdigital.erp.estoque.entity.SituacaoNumeroSerie;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumeroSerieRepository extends JpaRepository<NumeroSerie, Long> {

    Page<NumeroSerie> findByProdutoId(Long produtoId, Pageable pageable);

    Page<NumeroSerie> findByDepositoIdAndSituacao(Long depositoId, SituacaoNumeroSerie situacao, Pageable pageable);

    Optional<NumeroSerie> findByProdutoIdAndNumeroSerie(Long produtoId, String numeroSerie);
}
