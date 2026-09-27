package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.ProdutoFaixaPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProdutoFaixaPrecoRepository extends JpaRepository<ProdutoFaixaPreco, Long> {

    /**
     * Lista todas as faixas (ativas ou não) de um produto, ordenadas
     * por quantidade mínima crescente.
     * Útil para telas de cadastro/edição.
     */
    List<ProdutoFaixaPreco> findByIdProdutoOrderByQuantidadeMinimaAsc(Long idProduto);

    /**
     * Lista as faixas ATIVAS e VIGENTES de um produto na data informada,
     * ordenadas por quantidade mínima crescente.
     * É a query que o FaixaPrecoService usa pra calcular preço no PDV.
     *
     * <p>Vigência: DATA_INICIO &lt;= data &lt;= DATA_FIM (NULLs significam "sempre")
     */
    @Query("""
        SELECT f FROM ProdutoFaixaPreco f
         WHERE f.idProduto = :idProduto
           AND f.ativo = 'S'
           AND (f.dataInicio IS NULL OR f.dataInicio <= :data)
           AND (f.dataFim    IS NULL OR f.dataFim    >= :data)
         ORDER BY f.quantidadeMinima ASC
        """)
    List<ProdutoFaixaPreco> findVigentes(@Param("idProduto") Long idProduto,
                                         @Param("data") LocalDate data);
}