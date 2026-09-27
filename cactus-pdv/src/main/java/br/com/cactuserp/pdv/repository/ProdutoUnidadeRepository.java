package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.ProdutoUnidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoUnidadeRepository extends JpaRepository<ProdutoUnidade, Long> {
}
