package br.com.cactuserp.pdv.repository;

import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvSangria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de sangrias ({@link PdvSangria}).
 */
@Repository
public interface PdvSangriaRepository extends JpaRepository<PdvSangria, Long> {

    /** Lista todas as sangrias do movimento em ordem cronológica. */
    List<PdvSangria> findByPdvMovimentoOrderByIdAsc(PdvMovimento pdvMovimento);

    /**
     * Soma o valor de todas as sangrias do movimento. Usado pelo
     * fechamento de caixa (relatório Z).
     *
     * <p>Retorna {@code Optional.empty()} se não houver registros.
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT COALESCE(SUM(s.valor), 0) FROM PdvSangria s "
                    + "WHERE s.pdvMovimento = :movimento")
    Optional<BigDecimal> somarValorTotal(
            @org.springframework.data.repository.query.Param("movimento") PdvMovimento movimento);
}