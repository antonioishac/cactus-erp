package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.ProdutoUnidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoUnidadeRepository extends JpaRepository<ProdutoUnidade, Long> {

    Page<ProdutoUnidade> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
}
