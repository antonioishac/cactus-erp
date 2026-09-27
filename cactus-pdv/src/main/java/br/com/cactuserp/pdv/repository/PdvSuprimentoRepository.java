package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvSuprimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de suprimentos ({@link PdvSuprimento}).
 */
@Repository
public interface PdvSuprimentoRepository extends JpaRepository<PdvSuprimento, Long> {

    /** Lista todos os suprimentos do movimento em ordem cronológica. */
    List<PdvSuprimento> findByPdvMovimentoOrderByIdAsc(PdvMovimento pdvMovimento);

    /**
     * Soma o valor de todos os suprimentos do movimento. Usado pelo
     * fechamento de caixa (relatório Z).
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT COALESCE(SUM(s.valor), 0) FROM PdvSuprimento s "
                    + "WHERE s.pdvMovimento = :movimento")
    Optional<BigDecimal> somarValorTotal(
            @org.springframework.data.repository.query.Param("movimento") PdvMovimento movimento);
}