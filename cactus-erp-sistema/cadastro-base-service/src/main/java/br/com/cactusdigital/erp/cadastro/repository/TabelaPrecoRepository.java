package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.TabelaPreco;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TabelaPrecoRepository extends JpaRepository<TabelaPreco, Long> {

    Page<TabelaPreco> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /** {@code desde} = cursor de sincronização incremental (nulo = sem filtro). */
    @Query("SELECT t FROM TabelaPreco t WHERE (:desde IS NULL OR t.dataAtualizacao > :desde)")
    Page<TabelaPreco> buscarDesde(@Param("desde") Instant desde, Pageable pageable);

    @Query("SELECT DISTINCT t FROM TabelaPreco t LEFT JOIN FETCH t.produtos p LEFT JOIN FETCH p.produto WHERE t.id = :id")
    Optional<TabelaPreco> buscarComProdutosPorId(@Param("id") Long id);

    /** Evita N+1 na coleção `produtos` que {@code TabelaPrecoResponse} expõe numa
     * listagem paginada — chamada só pelo efeito colateral de hidratar a coleção nas
     * entidades já gerenciadas da página, ver {@code ClienteRepository#hidratarEnderecos}. */
    @Query("SELECT DISTINCT t FROM TabelaPreco t LEFT JOIN FETCH t.produtos p LEFT JOIN FETCH p.produto WHERE t.id IN :ids")
    List<TabelaPreco> hidratarProdutos(@Param("ids") List<Long> ids);
}
