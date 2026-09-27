package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdvTotalTipoPagamentoRepository
        extends JpaRepository<PdvTotalTipoPagamento, Long> {

    /**
     * Lista todos os pagamentos lançados num cupom, em ordem de inserção.
     * Útil pra:
     * <ul>
     *   <li>Reimpressão de cupom</li>
     *   <li>Telas de consulta/conferência de venda</li>
     *   <li>Auditoria fiscal</li>
     * </ul>
     */
    List<PdvTotalTipoPagamento> findByIdPdvVendaCabecalhoOrderByIdAsc(Long idPdvVendaCabecalho);

    /**
     * Lista todos os pagamentos de um cupom (cabeçalho), em ordem de
     * lançamento, com o TipoPagamento já carregado (sem LazyInit).
     *
     * <p>Usado pela tela de Consulta de Cupom, que renderiza os
     * pagamentos fora da transação.
     */
    @Query("SELECT p FROM PdvTotalTipoPagamento p "
            + "JOIN FETCH p.pdvTipoPagamento "
            + "WHERE p.idPdvVendaCabecalho = :idCupom "
            + "ORDER BY p.id ASC")
    List<PdvTotalTipoPagamento>
    findByIdPdvVendaCabecalho(@Param("idCupom") Long idCupom);
}