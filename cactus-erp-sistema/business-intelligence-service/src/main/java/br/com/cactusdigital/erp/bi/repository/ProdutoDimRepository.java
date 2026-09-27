package br.com.cactusdigital.erp.bi.repository;

import br.com.cactusdigital.erp.bi.entity.ProdutoDim;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoDimRepository extends JpaRepository<ProdutoDim, Long> {

    Optional<ProdutoDim> findByProdutoId(Long produtoId);

    List<ProdutoDim> findByProdutoIdIn(List<Long> produtoIds);
}
