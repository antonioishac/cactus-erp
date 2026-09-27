package br.com.cactusdigital.erp.crm.repository;

import br.com.cactusdigital.erp.crm.entity.Oportunidade;
import br.com.cactusdigital.erp.crm.entity.SituacaoOportunidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, Long> {

    /** Usado pela visão de funil (Kanban) — só oportunidades ainda abertas. */
    List<Oportunidade> findBySituacaoOrderByEstagioFunil_OrdemAsc(SituacaoOportunidade situacao);
}
