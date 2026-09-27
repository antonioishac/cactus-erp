package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByGtin(String codigo);

    boolean existsByGtin(String codigo);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Produto> buscarPorDescricao(@Param("descricao") String descricao);

    Optional<Produto> findByCodigoBalanca(Integer codigo);
}