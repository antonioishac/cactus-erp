package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvDevolucao;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PdvDevolucaoRepository extends JpaRepository<PdvDevolucao, Long> {

    List<PdvDevolucao> findByVendaDetalheOrderByIdAsc(PdvVendaDetalhe vendaDetalhe);

    /**
     * Soma da quantidade já devolvida de um item — usado pra validar que a
     * nova devolução não ultrapassa a quantidade original vendida.
     */
    @Query("SELECT COALESCE(SUM(d.quantidadeDevolvida), 0) FROM PdvDevolucao d "
            + "WHERE d.vendaDetalhe = :item")
    Optional<BigDecimal> somarQuantidadeDevolvida(@Param("item") PdvVendaDetalhe item);
}
