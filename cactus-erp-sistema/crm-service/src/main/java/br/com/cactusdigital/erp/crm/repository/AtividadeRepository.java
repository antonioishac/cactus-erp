package br.com.cactusdigital.erp.crm.repository;

import br.com.cactusdigital.erp.crm.entity.Atividade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    Page<Atividade> findByLead_Id(Long leadId, Pageable pageable);

    Page<Atividade> findByOportunidade_Id(Long oportunidadeId, Pageable pageable);
}
