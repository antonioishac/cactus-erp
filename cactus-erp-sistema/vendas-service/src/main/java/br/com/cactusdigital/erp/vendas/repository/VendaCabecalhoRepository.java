package br.com.cactusdigital.erp.vendas.repository;

import br.com.cactusdigital.erp.vendas.entity.SituacaoVenda;
import br.com.cactusdigital.erp.vendas.entity.VendaCabecalho;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendaCabecalhoRepository extends JpaRepository<VendaCabecalho, Long> {

    Page<VendaCabecalho> findByClienteId(Long clienteId, Pageable pageable);

    /** Usado pelos relatórios de vendas (Fase 17) — só vendas efetivamente concluídas. */
    List<VendaCabecalho> findBySituacaoAndDataVendaBetween(SituacaoVenda situacao, LocalDate dataInicio, LocalDate dataFim);

    /** {@code LEFT JOIN FETCH} pra evitar N+1 na coleção {@code itens} — ver
     * {@link br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport}. */
    @Query("SELECT DISTINCT v FROM VendaCabecalho v LEFT JOIN FETCH v.itens WHERE v.id IN :ids")
    List<VendaCabecalho> buscarComItens(@Param("ids") List<Long> ids);
}
