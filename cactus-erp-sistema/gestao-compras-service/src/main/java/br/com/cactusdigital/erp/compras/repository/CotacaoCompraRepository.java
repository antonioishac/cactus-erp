package br.com.cactusdigital.erp.compras.repository;

import br.com.cactusdigital.erp.compras.entity.CotacaoCompra;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CotacaoCompraRepository extends JpaRepository<CotacaoCompra, Long> {

    Page<CotacaoCompra> findByRequisicaoId(Long requisicaoId, Pageable pageable);

    /** {@code LEFT JOIN FETCH} de {@code fornecedores} — o fetch de {@code itens} de
     * cada fornecedor é feito à parte, em {@code FornecedorCotacaoRepository}
     * (não dá pra fazer os dois na mesma query: Hibernate lança
     * {@code MultipleBagFetchException} pra duas coleções {@code List} buscadas juntas,
     * mesmo encadeadas pai→filho, não só irmãs do mesmo pai). Ver
     * {@code CotacaoCompraService#buscarComFornecedoresEItens} e
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT c FROM CotacaoCompra c LEFT JOIN FETCH c.fornecedores WHERE c.id IN :ids")
    List<CotacaoCompra> buscarComFornecedores(@Param("ids") List<Long> ids);
}
