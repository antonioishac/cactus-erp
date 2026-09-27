package br.com.cactusdigital.erp.estoque.repository;

import br.com.cactusdigital.erp.estoque.entity.ProdutoKitComponente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoKitComponenteRepository extends JpaRepository<ProdutoKitComponente, Long> {

    List<ProdutoKitComponente> findByProdutoKitId(Long produtoKitId);

    void deleteByProdutoKitId(Long produtoKitId);
}
