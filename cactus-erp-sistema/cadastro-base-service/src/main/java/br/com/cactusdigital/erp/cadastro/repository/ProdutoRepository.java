package br.com.cactusdigital.erp.cadastro.repository;

import br.com.cactusdigital.erp.cadastro.entity.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    /** Evita N+1 nas 2 coleções (`faixasPreco`/`imagens`) que {@code ProdutoResponse}
     * expõe — 2 queries separadas (não dá pra buscar as duas juntas: Hibernate lança
     * {@code MultipleBagFetchException} pra duas coleções {@code List} bag da mesma
     * entidade), cada uma chamada só pelo efeito colateral de hidratar a coleção nas
     * entidades já gerenciadas da página — ver
     * {@code ClienteRepository#hidratarEnderecos} pro mesmo princípio. */
    @Query("SELECT DISTINCT p FROM Produto p LEFT JOIN FETCH p.faixasPreco WHERE p.id IN :ids")
    List<Produto> hidratarFaixasPreco(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT p FROM Produto p LEFT JOIN FETCH p.imagens WHERE p.id IN :ids")
    List<Produto> hidratarImagens(@Param("ids") List<Long> ids);
}
