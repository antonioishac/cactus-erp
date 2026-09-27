package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.ProdutoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Long> {

    /** Busca direto a imagem principal sem precisar carregar a lista toda. */
    @Query("""
        SELECT pi FROM ProdutoImagem pi
         WHERE pi.produto.id = :idProduto
           AND pi.principal = 'S'
           AND pi.situacao = 'A'
        """)
    Optional<ProdutoImagem> findImagemPrincipal(@Param("idProduto") Long idProduto);
}
