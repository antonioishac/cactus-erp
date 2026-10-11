package br.com.cactusdigital.erp.crm.repository;

import br.com.cactusdigital.erp.crm.entity.Oportunidade;
import br.com.cactusdigital.erp.crm.entity.SituacaoOportunidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Long> {

    /** Usado pela visão de funil (Kanban) — só oportunidades ainda abertas. */
    List<Oportunidade> findBySituacaoOrderByEstagioFunil_OrdemAsc(SituacaoOportunidade situacao);

    /**
     * Variante pro relatório de funil — {@code situacao} opcional (null = todas), com
     * {@code JOIN FETCH} de {@code estagioFunil} (singular, resolve
     * {@code estagioFunilNome} sem N+1 — diferente de {@link #findBySituacaoOrderByEstagioFunil_OrdemAsc},
     * usado só pelo Kanban, que não faz esse fetch porque o `ORDER BY` por associação
     * adiciona o `JOIN` só pra ordenação, sem popular o proxy).
     */
    @Query("SELECT o FROM Oportunidade o LEFT JOIN FETCH o.estagioFunil WHERE (:situacao IS NULL OR o.situacao = :situacao) ORDER BY o.estagioFunil.ordem ASC")
    List<Oportunidade> buscarParaRelatorio(@Param("situacao") SituacaoOportunidade situacao);
}
